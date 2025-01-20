

# Update 01/2025: moved (quite some time ago) from Aurora to Github
# Note: As there are many patches, we need to expliclty say here them again, see overridden
# recipe in meta-freescale/recipes-bsp/imx-mkimage
SRC_URI = "git://github.com/nxp-imx/imx-mkimage.git;protocol=https;branch=${SRCBRANCH} \
           file://0001-mkimage_fit_atf-fix-fit-generator-node-naming.patch \
           file://0001-iMX8M-soc.mak-use-native-mkimage-from-sysroot.patch \
           file://0001-Add-support-for-overriding-BL32-and-BL33-not-only-BL.patch \
           file://0001-Add-LDFLAGS-to-link-step.patch \
           file://0001-Add-support-for-overriding-BL31-BL32-and-BL33.patch \
"
