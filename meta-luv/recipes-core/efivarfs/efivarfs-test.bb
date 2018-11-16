DESCRIPTION = "EFI varfs tests"
HOMEPAGE = "https://www.kernel.org/pub/linux/kernel"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${STAGING_KERNEL_DIR}/COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
KBRANCH="stable"

# Picking up matts branch
SRC_URI = "file://luv-parser-efivarfs \
           file://efivarfs \
           file://efivarfs.json"

#we need some of the stuff below
DEPENDS_class-native += "qemu-native"
inherit autotools luv-test
DEPENDS = "virtual/kernel"
RDEPENDS_${PN} += "e2fsprogs bash"

S="${STAGING_KERNEL_DIR}"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_configure[noexec] = "1"
do_clean[noexec] = "1"

EXTRA_OEMAKE = " \
    CC='${CC}' \
    -C ${S}/tools/testing/selftests/efivarfs"

# This is to just to satisfy the compilation error
#I am not sure why I am getting this
FILES_${PN}-dbg += "/usr/share/efivarfs-test/.debug"

#This is the compilation area
#we need to compile the self tests
do_compile() {
    unset CFLAGS
    oe_runmake
}

#Installing is nothing but putting things in place
do_install() {
    # Creating a directory
    install -d ${D}${datadir}/efivarfs-test

    #Copying some of the files, these are part of the linux code
    install -m 0755 ${S}/tools/testing/selftests/efivarfs/create-read ${D}${datadir}/efivarfs-test
    install -m 0755 ${S}/tools/testing/selftests/efivarfs/open-unlink ${D}${datadir}/efivarfs-test
    install -m 0755 ${S}/tools/testing/selftests/efivarfs/efivarfs.sh ${D}${datadir}/efivarfs-test

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/efivarfs ${D}${bindir}
}

do_cleanall() {
    cd ${S}/tools/testing/selftests/efivarfs
    oe_runmake clean
}
LUV_TEST_LOG_PARSER="luv-parser-efivarfs"
LUV_TEST_JSON="efivarfs.json"
LUV_TEST="efivarfs"
