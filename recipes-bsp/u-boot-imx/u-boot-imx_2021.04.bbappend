
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Add-Engicam-patches-for-Engicam-boards-supports.patch "

# Patches to be added when SecureBoot is enabled
SRC_URI:append:csfsigned = " file://0002-GWC_HPC_customizations.patch "

SRC_URI:append = " file://0003-GWC_HPC_config_cleanup.patch "
