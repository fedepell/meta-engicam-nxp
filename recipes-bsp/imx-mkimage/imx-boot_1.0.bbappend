FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

require imx-mkimage_git_hig.inc

# FP: Temporarily removed, is this really needed?
# SRC_URI += "file://0001-fix-dtb-name.patch"

SRC_URI += "file://csf_boot_image.txt "

do_compile() {
    compile_${SOC_FAMILY}
    # Copy TEE binary to SoC target folder to mkimage
    if ${DEPLOY_OPTEE}; then
        cp ${DEPLOY_DIR_IMAGE}/tee.bin  ${BOOT_STAGING}
    fi
    # mkimage for i.MX8
    for target in ${IMXBOOT_TARGETS}; do
        if [ "$target" = "flash_linux_m4_no_v2x" ]; then
           # Special target build for i.MX 8DXL with V2X off
           bbnote "building ${SOC_TARGET} - ${REV_OPTION} V2X=NO ${target}"
           make SOC=${SOC_TARGET} ${REV_OPTION} V2X=NO  flash_linux_m4
        else
           bbnote "building ${SOC_TARGET} - ${REV_OPTION} ${target}"
           make SOC=${SOC_TARGET} ${REV_OPTION} ${target} dtbs=${UBOOT_DTB_NAME}
        fi
        if [ -e "${BOOT_STAGING}/flash.bin" ]; then
            cp ${BOOT_STAGING}/flash.bin ${S}/${BOOT_CONFIG_MACHINE}-${target}
        fi
    done
}

do_compile:append:csfsigned() {
    # Secure boot signing with NXP CSF tool (if csfsigned in overrides is set)
    # Notes:
    #  1) Need to download (after registration) NXP CSF tool (the one in Yocto is *not* for MX8)
    #  2) CSF requires run in its own directory (as uses relative paths) so a bit clumsy (need to cd there, work, copy back)
    #  3) Define the CSFPATH in the machine configuration
    #
    if [ -n "${CSFPATH}" ]; then
      # Copy unsigned image to CSF dir
      cp ${S}/${BOOT_CONFIG_MACHINE}-${target} ${CSFPATH}/flash.bin
      # Copy configuration file to CSF dir
      cp ${S}/../csf_boot_image.txt ${CSFPATH}
      # Move there and sign
      cd ${CSFPATH}
      ./linux64/bin/cst -i csf_boot_image.txt -o flash.signed.bin
      # Signed back to target dir
      cp flash.signed.bin ${S}/${BOOT_CONFIG_MACHINE}-${target}

      # Install the mkimage tool to BOOT_TOOLS so Linux kernel signing can use it (deploy is too late)
      install -d ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}
      install -m 0755 ${B}/mkimage_imx8 ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/mkimage_imx8.csf
    fi

}
