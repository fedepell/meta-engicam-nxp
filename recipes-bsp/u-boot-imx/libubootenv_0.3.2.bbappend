FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://fw_env.config"

do_install:append () {
        install -d ${D}/etc
        install -m 0755 ${WORKDIR}/fw_env.config ${D}/etc/fw_env.config
}
