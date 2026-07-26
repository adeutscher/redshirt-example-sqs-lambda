#!/bin/bash
set -euo pipefail

PACKAGE_OLD="com.redshirt.example.sqslambda"
ARTIFACT_OLD="redshirt-example-sqs-lambda"
PACKAGE_NEW="${1:-}"

if [ -z "${PACKAGE_NEW}" ]; then
  echo "Usage: bash init-repo.sh <new.package.name>"
  echo "Example: bash init-repo.sh com.acme.orders.sqslambda"
  exit 1
fi

# Derive Maven artifact base from package: com.acme.foo -> acme-foo
ARTIFACT_NEW="$(echo "${PACKAGE_NEW}" | sed -E 's/^com\.//; s/\./-/g' | tr '[:upper:]' '[:lower:]')"

PACKAGE_PATH_OLD="${PACKAGE_OLD//.//}"
PACKAGE_PATH_NEW="${PACKAGE_NEW//.//}"

rename_module_dir() {
  local path="$1"
  local dir
  dir="$(dirname "${path}")"
  local name
  name="$(basename "${path}")"
  local name_new
  name_new="$(sed "s/${ARTIFACT_OLD}/${ARTIFACT_NEW}/g" <<< "${name}")"
  if [ "${name}" != "${name_new}" ]; then
    mv "${path}" "${dir}/${name_new}"
  fi
}

echo "Renaming package ${PACKAGE_OLD} -> ${PACKAGE_NEW}"
echo "Renaming artifact ${ARTIFACT_OLD} -> ${ARTIFACT_NEW}"

# Rename module directories (deepest first)
while read -r f; do
  [ -n "${f}" ] || continue
  rename_module_dir "${f}"
done <<< "$(find . -type d -name "${ARTIFACT_OLD}*" | awk '{ print length, $0 }' | sort -rn | cut -d' ' -f2-)"

# Move Java source trees to the new package path
while read -r src_root; do
  [ -n "${src_root}" ] || continue
  old_pkg_dir="${src_root}/${PACKAGE_PATH_OLD}"
  new_pkg_dir="${src_root}/${PACKAGE_PATH_NEW}"
  if [ -d "${old_pkg_dir}" ]; then
    mkdir -p "$(dirname "${new_pkg_dir}")"
    mv "${old_pkg_dir}" "${new_pkg_dir}"
    # Remove empty leftover package directories under src_root
    find "${src_root}" -type d -empty -delete 2>/dev/null || true
  fi
done <<< "$(find . -type d \( -path '*/src/main/java' -o -path '*/src/test/java' \))"

# String replace across project files
while read -r f; do
  [ -n "${f}" ] || continue
  sed -i \
    -e "s/${PACKAGE_OLD}/${PACKAGE_NEW}/g" \
    -e "s/${ARTIFACT_OLD}/${ARTIFACT_NEW}/g" \
    "${f}"
done <<< "$(find . \( -name '*.java' -o -name 'pom.xml' -o -name '*.md' -o -name '*.sh' -o -name 'Dockerfile' -o -name '*.yaml' -o -name '*.yml' -o -name '*.xml' \) -not -path './.git/*')"

echo "Done."
