PR="r1"

# fail2ban has an explicit dependency on busybox-syslog
# which, in turn, conflicts with rsyslog. 
# we'd have to forcibly remove it or the newer gwc core
# won't be installable.
VIRTUAL-RUNTIME_syslog = ""
VIRTUAL-RUNTIME_base-utils-syslog = ""

# moving fail2ban conf to avoid overwriting gwc-conf
do_install:append() {
  mv "${D}/etc/fail2ban/fail2ban.conf" \
     "${D}/etc/fail2ban/fail2ban.conf.dist"
}