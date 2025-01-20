
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Add_Engicam_dts.patch file://0002-Add-customized-DTS-for-HPC-hardware-Dual-and-Quad.patch file://0003-Add_external_RTC.patch file://0004-Hardware_rev2_pins.patch file://0005-Add_CAAM_blob_dts.patch file://csf_linux_img.txt "

# Custom Higeco Version ID
PV = "${LINUX_VERSION}-HSC-1"
PR = "r1"

# After last (end of 2024) poky upgrade we had RPM packages that had a different version
# name due to various changes (in a stable / maintenance branch ???) in kernel version
# management in Yocto. See (in time order, note some override/delete each other):
# - https://git.yoctoproject.org/poky/commit/meta/classes?h=kirkstone&id=552288e0c83084097cf38611bd82315aa9d892df
# - https://git.yoctoproject.org/poky/commit/meta/classes?h=kirkstone&id=0b39955d14600257a6eafc211fd68a933c69a0e9
# - https://git.yoctoproject.org/poky/commit/meta/classes?h=kirkstone&id=57b8a1adb54a3adab22f4e04ced66a92ffc78a96
# - https://git.yoctoproject.org/poky/commit/meta/classes?h=kirkstone&id=2b7c113459a602c91badaa7543d02811feac0151
# - https://git.yoctoproject.org/poky/commit/meta/classes?h=kirkstone&id=26f23535eef1f3314c42cd00cda0c6da7cdaf9af
# Setting local version as below is a half-hack to guarantee generated RPMs are named the
# same as before, so dnf update will work correctly (otherwise we would need to remove and
# then install packages which would be annoying and risky)
# (it is set to "+" as this was generated, as can be deduced from commits above, due to
# local changes present, ie. patches we apply, and git reporting them)
LOCALVERSION = "+"

# Additional parts to generate container and sign it (if csfsigned override is present)
# See also imx-boot recipe for more details!
DEPENDS:csfsigned += "imx-boot openssl-native"

BOOT_TOOLS = "imx-boot-tools"

do_install:append:csfsigned() {
    if [ -n "${SIGN_KERNEL}" ]; then
      if [ -n "${CSFPATH}" ]; then
         # Copy CSF configuration part
         cp ${WORKDIR}/csf_linux_img.txt ${CSFPATH}
         # Create container with kernel and DTB
         ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/mkimage_imx8.csf -soc QX -rev B0 -c -ap arch/arm64/boot/Image a53 0x80280000 --data arch/arm64/boot/dts/engicam/imx8xq-icore-starterkit.dtb a53 0x83000000 -out ${CSFPATH}/flash_os.bin
         # Sign image in container with CSF tool
         cd ${CSFPATH}
         ./linux64/bin/cst -i csf_linux_img.txt -o os_cntr_signed.bin
         # Copy for delivery in package and in images for WIC
         install -m 0644 os_cntr_signed.bin ${D}/boot
         # This would be better to have it in a do_deploy I believe
         cp os_cntr_signed.bin ${DEPLOY_DIR_IMAGE}
         # Delete unsigned image now, so it will not be packed in image-image automatically
         rm ${D}/boot/Image-*
      fi
    fi
}

# Remove the dynamically generated (in kernel.bbclass) post-install script for RPM (generates links that break on FAT)
python write_specfile:prepend:csfsigned() {
    d.setVar('pkg_postinst:kernel-image-image', "")
}


# Add it to the image RPM (the image-image is dynamically created in kernel.bbclass, so we cannot put the file in that one)
FILES:${KERNEL_PACKAGE_NAME}-image:csfsigned += "/boot/os_cntr_signed.bin"
