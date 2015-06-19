xquery version "3.0";

import module namespace repo='http://exist-db.org/xquery/repo';

let $exists := repo:list() = 'http://freelibrary.info/xquery/marc'
return (
  '[SCRIPT] Removed old XAR file: ' || (if ($exists) then repo:remove('http://freelibrary.info/xquery/marc') else false()),
  '[SCRIPT] Installed new XAR file: ' || repo:install('http://localhost:${http.port}/${project.artifactId}-${project.version}.xar')
)