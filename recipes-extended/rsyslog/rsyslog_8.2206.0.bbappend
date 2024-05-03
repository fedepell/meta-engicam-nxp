PR = "r1"

# We don't want silly logrotate deps
# As no one is currently using it
# The same is valid for systemd service
# as we provide our-own
RDEPENDS:${PN}:remove = "logrotate"
RCONFLICTS:${PN} = ""
SYSTEMD_SERVICE:${PN} = ""

# Removing some not needed files
# (logrotate, conf and systemd services)
do_install:append() {
  rm -rf "${D}/etc" \
         "${D}/lib"
}
