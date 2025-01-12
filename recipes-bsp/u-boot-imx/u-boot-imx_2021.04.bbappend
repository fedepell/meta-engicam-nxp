
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


# Update 01/2025: moved (quite some time ago) from Aurora to Github
SRC_URI = "git://github.com/nxp-imx/uboot-imx.git;protocol=https;branch=${SRCBRANCH} \
           file://0001-Add-Engicam-patches-for-Engicam-boards-supports.patch "

# Patches to be added when SecureBoot is enabled
SRC_URI:append:csfsigned = " file://0002-GWC_HPC_customizations.patch "

SRC_URI:append = " file://0003-GWC_HPC_config_cleanup.patch file://0004-UBoot_password_enable.patch file://0005-Reset_eth1.patch file://0006-Reset_eth1_dtbs.patch "
