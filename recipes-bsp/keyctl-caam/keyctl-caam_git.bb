SUMMARY = "NXP CAAM Key management tool"
DESCRIPTION = "NXP CAAM Key management tool"
SECTION = "base"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=8636bd68fc00cc6a3809b7b58b45f982"

SRCBRANCH = "lf-5.10.y_1.0.0"
SRC_URI = "git://github.com/nxp-imx/keyctl_caam;branch=${SRCBRANCH}"
SRCREV = "6b80882e3d5bc986a1f2f9512845170658ba9ea2"

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
	oe_runmake DESTDIR=${D} install
}

COMPATIBLE_MACHINE = "(mx8qm-nxp-bsp|mx8qxp-nxp-bsp|mx8dxl-nxp-bsp|mx8dx-nxp-bsp)"
