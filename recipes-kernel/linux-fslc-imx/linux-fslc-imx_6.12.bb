# Released under the MIT license (see COPYING.MIT for the terms)
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Linux Kernel provided by NXP and supported by Community"
DESCRIPTION = "Linux Kernel provided by NXP as the part of release distribution. \
Main focus is set on i.MX Family Reference Boards. \
It includes support for many NXP Proprietary IPs (GPU, VPU, IPU). \
Latest stable Kernel patchlevel is applied and maintained by Community."

###############################################################################
# This recipe (and corresponding kernel repository and branch) receives updates
# from 3 different sources:
# 1. Stable [linux-6.6.y] branch updates of korg;
# 2. NXP-specific updates via branch [lf-6.6.y] shared via GitHub NXP repo;
# 3. Critical patches, which are not (yet) integrated into either of 2 above
#    sources, but are required to be applied to the kernel tree.
#
# Therefore, there is a need to keep track on the patches which are introduced
# from every source. This could be achieved in this recipe by filling the
# below list with the information once the update is performed from any source.
#
# Once the critical patch gets merged into the stable branch, or NXP-specific
# patches would be covered by the tag - individual entries from sections below
# could be removed.
#
# ------------------------------------------------------------------------------
# 1. Stable (tag or SHA(s))
# ------------------------------------------------------------------------------
#    tag: v6.12.3
#
# ------------------------------------------------------------------------------
# 2. NXP-specific (tag or SHA(s))
# ------------------------------------------------------------------------------
#    tag: lf-6.12.3-1.0.0
#
# ------------------------------------------------------------------------------
# 3. Critical patches (SHA(s))
# ------------------------------------------------------------------------------
# The list includes well-known commits not yet upstreamed. Reverts address merge
# conflicts, prioritizing NXP BSP source code as the latest vendor updates.
# Additional commits may exist to better acommodate yocto builds.
#
# $ git log --oneline  --no-merges v6.12.49.. ^mainline/linux-6.12.y ^NXP/lf-6.12.y
# - bacd5504126bb imx8mp-olimex.dts: CSI GPIO pins
# - 3a7012e991c98 hwrng: optee: support generic crypto
# - 6c0a3377748eb arm64: dts: imx8mq: drop cpu-idle-states
# - 7db0692d9ff5e of: enable using OF_DYNAMIC without OF_UNITTEST
# - eff98b934385c gpu: drm: cadence: select hdmi helper
# - be5e175e43d93 imx:dts:imx8mm-evkb: fix the pmic name to avoid duplicated label error
# - 76e18f5a57b3e arm64: dts: imx8mm-evk-qca-wifi: enable support for bluetooth
# - 06b99391f850c drm: of: Fix build without CONFIG_OF
# - 17ac89e381a9d i2c: imx: Remove unnecessary clock reconfiguration
# - 6d157e81ccc53 drm/imx: lcdifv3: Fix videomode settings
# - 0a355239e2df3 clk: imx: imx8qm: add more resources to whitelist
# - c5c4869899b1c arm64: dts: imx8: img: add #address-cells and #size-cells to I2C MIPI CSI nodes
# - 3159e7d086295 arm64: dts: imx8qm: add missing imx8-ss-cm40.dtsi include
# - ffea393034d48 arm64: imx_v8_defconfig: Enable CONFIG_GPIO_VF610
# - a8762ad609202 imx8mp-olimex.dts: Olimex iMX8MP-SOM-EVB-IND
#
# NOTE to upgraders:
# This recipe should NOT collect individual patches, they should be applied to
# the linux-fslc kernel tree on the corresponding branch, and tracking
# information should be properly filled in above.
###############################################################################

require linux-imx.inc

KBRANCH = "6.12-2.0.x-imx"
SRC_URI = "git://github.com/Freescale/linux-fslc.git;branch=${KBRANCH};protocol=https"
SRCREV = "6667f0d0b974e091050b84f1e66af608dbd590c6"

SRC_URI += "file://defconfig file://0001-Add_Engicam_HSC_dts.patch file://csf_linux_img.txt "


# PV is defined in the base in linux-imx.inc file and uses the LINUX_VERSION definition
# required by kernel-yocto.bbclass.
#
# LINUX_VERSION define should match to the kernel version referenced by SRC_URI and
# should be updated once patchlevel is merged.
LINUX_VERSION = "6.12.55"

# Custom Higeco Version ID
PV = "${LINUX_VERSION}-HSC-1"
PR = "r2"
LOCALVERSION = "+"


# KBUILD_DEFCONFIG:mx6-generic-bsp = "imx_v7_defconfig"
# KBUILD_DEFCONFIG:mx7-generic-bsp = "imx_v7_defconfig"
# KBUILD_DEFCONFIG:mx8-generic-bsp = "imx_v8_defconfig"
# KBUILD_DEFCONFIG:mx9-generic-bsp = "imx_v8_defconfig"

# Local version indicates the branch name in the NXP kernel tree where patches are collected from.
DEFAULT_PREFERENCE = "1"

COMPATIBLE_MACHINE = "(imx-generic-bsp)"

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

# ====================================================
# 2026-02-25 - Obsoleting old kernel package
#
# Kernel packages writes always the same file
# so we need to make sure that DNF will not
# try to install both old and new package at
# the same time, and will not try to keep
# both of them in the system.
# ====================================================

# The name of the old package you want to obsolete
OLD_PKG_NAME = "kernel-image-5.10.109-hsc-1+"

# 1. RREPLACES tells the package manager this package replaces the old one (Maps to RPM 'Obsoletes')
RREPLACES:${KERNEL_PACKAGE_NAME}-image += "${OLD_PKG_NAME}"

# 2. RCONFLICTS prevents both packages from being installed at the same time
RCONFLICTS:${KERNEL_PACKAGE_NAME}-image += "${OLD_PKG_NAME}"

# 3. RPROVIDES ensures anything depending on the old package will be satisfied by this new one
RPROVIDES:${KERNEL_PACKAGE_NAME}-image += "${OLD_PKG_NAME}"
