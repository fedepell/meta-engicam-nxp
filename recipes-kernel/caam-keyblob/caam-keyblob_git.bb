SUMMARY = "NXP Cryptographic Acceleration and Assurance Module (CAAM) - Linux driver"
DESCRIPTION = "NXP Cryptographic Acceleration and Assurance Module (CAAM) - Linux driver"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb8e25572881b205d17b185fd40fb5e3"

PR="r1"

inherit module

SRC_URI = "git://github.com/usbarmory/caam-keyblob;protocol=https;branch=master file://0001-Patch_arch_no_v7.patch"
SRCREV = "2ba70f052fb19afde951ab8eddb110a561cc6572"


S = "${WORKDIR}/git"

RPROVIDES:${PN} += "kernel-module-caam-keyblob"
