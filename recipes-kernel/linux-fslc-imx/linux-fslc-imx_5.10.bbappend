
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"



SRC_URI += "file://0001-Add_Engicam_dts.patch file://0002-Add-customized-DTS-for-HPC-hardware-Dual-and-Quad.patch file://csf_linux_img.txt "


# Additional parts to generate container and sign it (if csfsigned override is present)
# See also imx-boot recipe for more details!
DEPENDS:csfsigned += "imx-boot"

BOOT_TOOLS = "imx-boot-tools"

do_install:append:csfsigned() {
    if [ -n "${SIGN_KERNEL}" ]; then
      if [ -n "${CSFPATH}" ]; then
         # Copy CSF configuration part
         cp ${S}/../csf_linux_img.txt ${CSFPATH}
         # Create container with kernel and DTB
         ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/mkimage_imx8.csf -soc QX -rev B0 -c -ap arch/arm64/boot/Image a53 0x80280000 --data arch/arm64/boot/dts/engicam/imx8xq-icore-starterkit.dtb a53 0x83000000 -out ${CSFPATH}/flash_os.bin
         # Sign image in container with CSF tool
         cd ${CSFPATH}
         ./linux64/bin/cst -i csf_linux_img.txt -o os_cntr_signed.bin
         # Copy for delivery in package and in images for WIC
         install -m 0644 os_cntr_signed.bin ${D}/boot
         # This would be better to have it in a do_deploy I believe
         cp os_cntr_signed.bin ${DEPLOY_DIR_IMAGE}
      fi
    fi
}

# Should it go to -image or -image-image package? (TODO)
FILES:${KERNEL_PACKAGE_NAME}-image:csfsigned += "/boot/os_cntr_signed.bin"
