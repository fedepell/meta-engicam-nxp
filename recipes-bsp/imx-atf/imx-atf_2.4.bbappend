FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Update 01/2025: moved (quite some time ago) from Aurora to Github
SRC_URI = "git://github.com/nxp-imx/imx-atf.git;protocol=https;branch=${SRCBRANCH} \
           file://0001-mx8m-mini-remove-uart4-from-m4.patch"

