#!/bin/bash -u

set -e

TO_DIR=${PWD}/Results
LOCKFILE=${PWD}/continuous-build.lock

cd cqs/

if (set -o noclobber; echo "$$" > "${LOCKFILE}") 2> /dev/null; 
then
	trap 'rm -f "${LOCKFILE}"; exit $?' INT TERM EXIT
	
	mtn pull && mtn up
	HEAD=`mtn automate get_base_revision_id`
	TARGET=${TO_DIR}/${HEAD}
	mkdir -p ${TARGET}
	
	# Compile source, run tests and capture exit code:
	EXITCODE=0
	(time -p bash -c "ant clean compile_test && java -jar startup.jar test" > ${TARGET}/run.log 2>&1 || EXITCODE=$?) > ${TARGET}/time.txt 2>&1
	echo ${EXITCODE} > ${TARGET}/status
	USER_TIME=`grep real ${TARGET}/time.txt | cut -b 6-`
	
	# Add the new revision to the list of revisions:
	echo "${HEAD} ${EXITCODE} ${USER_TIME}" >> ${TO_DIR}/revisions.txt
	
	rm -f "${LOCKFILE}"
	trap - INT TERM EXIT
else
	echo "Failed to acquire lockfile: ${LOCKFILE}." 
	echo "Held by $(cat ${LOCKFILE})"
fi 
