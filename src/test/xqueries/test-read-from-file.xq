xquery version "3.0";

declare namespace marcxml="http://www.loc.gov/MARC21/slim";

import module namespace marc="http://freelibrary.info/xquery/marc";
import module namespace util="http://exist-db.org/xquery/util";
import module namespace file="http://exist-db.org/xquery/file";

declare function local:error($err-code, $err-message, $err-value) {
  if (exists($err-value))
  then '[' || $err-code || '] ' || $err-message || ": '" || $err-value || "'"
  else '[' || $err-code || '] ' || $err-message
};

((: Running a series of tests: some that are supposed to pass and others that are supposed to fail :)
try {
  let $marc := marc:read('src/test/resources/collection.mrc')
  let $result := count($marc//marcxml:leader) eq 2
  return
    if ($result)
    then '[SCRIPT] Successfully read in a MARC file containing two MARC records'
    else '[ERROR] Failed to successfully read in a MARC file containing two MARC records'
} catch * {
  '[ERROR] An unexpected error thrown while reading MARC records: ' || local:error($err:code, $err:description, $err:value)
},
try {
  let $marc := marc:read('src/test/resources/bad_leaders_10_11.mrc')
  let $result := count($marc//marcxml:leader) eq 2
  return
    if ($result)
    then '[ERROR] Successfully read a bad MARC record when it should have thrown an exception'
    else '[ERROR] Failed to successfully read in a MARC record, but did not throw an exception like it should have'
} catch * {
  '[SCRIPT] Successfully threw an exception: ' || local:error($err:code, $err:description, $err:value)
},
try {
  let $result := marc:store('src/test/resources/collection.mrc', 'marc-records')
  let $leaders := collection('/db/marc-records')//marcxml:leader
  return
    if ($result and (count($leaders) = 2))
    then '[SCRIPT] Successfully read in two MARC records and stored then as MARCXML in a new collection'
    else '[ERROR] Failed to successfully store two MARC records as MARCXML in a new collection'
} catch * {
  '[ERROR] Trying to write records into a collection threw an exception: ' || local:error($err:code, $err:description, $err:value)
}
)