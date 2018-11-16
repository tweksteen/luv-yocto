DESCRIPTION = "pstore tests"
HOMEPAGE = "https://www.kernel.org/pub/linux/kernel"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${STAGING_KERNEL_DIR}/COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
KBRANCH="stable"

# Picking up matts branch
SRC_URI = "file://pstore-test-runner \
           file://luv-parser-pstore-test \
           file://pstore-test.json"

#we need some of the stuff below
DEPENDS_class-native += "qemu-native"
inherit autotools luv-test
DEPENDS = "virtual/kernel"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_configure[noexec] = "1"
do_clean[noexec] = "1"

S="${STAGING_KERNEL_DIR}"

# If the LUV_STORAGE variable in luv_test_manager.bb ever changes,
# the reboot_dir variable should be changed accordingly
reboot_dir = "/mnt/luv-storage"

EXTRA_OEMAKE = " \
    CC='${CC}' \
    -C ${S}/tools/testing/selftests/pstore"

patch() {
       sed -i 's,REBOOT_DIR,${reboot_dir},g' ${S}/tools/testing/selftests/pstore/common_tests
       sed -i 's,REBOOT_DIR,${reboot_dir},g' ${WORKDIR}/luv-parser-pstore-test
       sed -i 's,REBOOT_DIR,${reboot_dir},g' ${WORKDIR}/pstore-test-runner
}

do_patch_append() {
    bb.build.exec_func('patch', d)
}

# Installing is nothing but putting things in place
do_install() {
    # Creating a directory
    install -d ${D}${datadir}/pstore-test

    # Copying some of the files, these are part of the linux code
    install -m 0755 ${S}/tools/testing/selftests/pstore/common_tests ${D}${datadir}/pstore-test
    install -m 0755 ${S}/tools/testing/selftests/pstore/pstore_crash_test ${D}${datadir}/pstore-test
    install -m 0755 ${S}/tools/testing/selftests/pstore/pstore_post_reboot_tests ${D}${datadir}/pstore-test
    install -m 0755 ${S}/tools/testing/selftests/pstore/pstore_tests ${D}${datadir}/pstore-test

    # This is the script which will run all the tests
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/pstore-test-runner ${D}${bindir}
}

do_cleanall() {
    cd ${S}/tools/testing/selftests/pstore
    oe_runmake clean
}

FILES_${PN} += "/usr/share/pstore-test/common_tests \
               /usr/share/pstore-test/pstore_crash_test \
               /usr/share/pstore-test/pstore_post_reboot \
               /usr/share/pstore-test/pstore_tests \
               ${bindir}/pstore-test-runner \
               "

LUV_TEST_LOG_PARSER="luv-parser-pstore-test"
LUV_TEST_JSON="pstore-test.json"
LUV_TEST="pstore-test-runner"
LUV_TEST_ARGS=""
