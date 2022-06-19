DESCRIPTION = "GWC image based on Engicam hardware"

LICENSE = "MIT"

inherit core-image

EXTRA_IMAGE_FEATURES = " debug-tweaks ssh-server-openssh package-management "

IMAGE_INSTALL:append = " \
	binutils \
	engicam-emmc-tools \
	gdbserver \
	strace \
	iproute2 \
	canutils \
	mtd-utils \
	devmem2 \
	i2c-tools \
	minicom \
	ethtool \
	dosfstools \
	e2fsprogs \
	usbutils \
	iw \
	wpa-supplicant \
	json-c \
	ppp \
	ppp-tools \
	curl \
	openvpn \
	lighttpd \
	lighttpd-module-openssl \
	lighttpd-module-rewrite \
	lighttpd-module-redirect \
	lighttpd-module-alias \
	lighttpd-module-auth \
	lighttpd-module-authn-file \
	lighttpd-module-evasive \
	lighttpd-module-usertrack \
	lighttpd-module-setenv \
	lighttpd-module-cgi \
	lighttpd-module-deflate \
	libmicrohttpd \
	ntpdate \
	sntp \
	ntp-utils \
	procps \
	xz \
	wget \
	tar \
	sysstat \
	screen \
	msmtp \
	lsof \
	iptables \
	gzip \
	grep \
	bzip2 \
	bash \
	dos2unix \
	nano \
	traceroute \
	hostapd \
	iproute2-ss \
	iproute2-devlink \
	iproute2-genl \
	iproute2-ifstat \
	iproute2-lnstat \
	iproute2-nstat \
	iproute2-rtacct \
	iproute2-tc \
	iproute2-tipc \
	libxml2-utils \
	sqlite3 \
	util-linux \
	avahi-utils \
	usb-modeswitch \
	htop \
	bind-utils \
	netcat \
	iputils \
	coreutils \
	firmware-imx-vpu-imx8 \
	dhcpcd \
	u-boot-fw-utils \
	libmodbus \
	gd \
	opendnp3 \
	libnodave \
	fswebcam \
	yasdi \
	btrfs-tools \
	rpm \
	gpgme \
	gnupg \
	cryptsetup \
"

# NOTE: lighttpd-mod-compress -> lighttpd-mod-deflate https://redmine.lighttpd.net/projects/1/wiki/docs_modcompress
#
# NOTE: yasdi -> contains a reference to HW RTS/CTS handling for 485, to be reviewed
#
# Obsolete Engicam stuff:
#	cantest
#	serialtools
#
# Removed:
#	mtd-utils-ubifs
#	imx-kobs
#


TOOLCHAIN_TARGET_TASK += " wpa-supplicant-staticdev libiec61850-staticdev lib60870-staticdev"
