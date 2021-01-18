
meta-engicam-mx8
================

Based on NXP Yocto Zeus 2.3 GA


```
mkdir imx-yocto-bsp
cd imx-yocto-bsp
repo init -u https://source.codeaurora.org/external/imx/imx-manifest -b imx-linux-zeus -m imx-5.4.70-2.3.0.xml
repo sync
```


Boards supported
----------------

- imx8mp-icore
- imx8mp-smarcore
- imx8mm-icore
- imx8mm-icore-2g


Images available
----------------

- engicam-evaluation-image



First build
-----------

```
DISTRO=eng-imx-xwayland MACHINE=imx8mp-icore source imx-setup-release.sh -b build
bitbake-layers add-layer ../sources/meta-engicam-mx8
bitbake engicam-image-test-hw
```
