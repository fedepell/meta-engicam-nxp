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
	libmicrohttpd \
	procps \
	xz \
	wget \
	tar \
	sysstat \
	screen \
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
	kernel-module-caam-keyblob \
	kernel-module-dm-crypt \
	keyutils \
	keyctl-caam \
	python3-fail2ban \
	nftables \
	net-snmp-dev \
	net-snmp-server \
	higeco-signing-keys-rpm \
	chrony \
	chronyc \
	libedit \
	gpsd \
	libgps \
	rsyslog \
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


TOOLCHAIN_TARGET_TASK += " wpa-supplicant-staticdev rpm rpm-build rpm-sign"
