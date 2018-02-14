<!--
{% comment %}
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to you under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
{% endcomment %}
-->
# Stream-SQL-TCK HOWTO

Here's some miscellaneous documentation about using and developing Stream-SQL-TCK.

# Release

Make sure that `mvn clean install site` runs on JDK 8, 9 and 10,
on Linux, macOS and Windows.
Also check [Travis CI](https://travis-ci.org/julianhyde/Stream-SQL-TCK).

Update the [release history](HISTORY.md),
the version number at the bottom of [README](README.md),
and the copyright date in [NOTICE](NOTICE).

Using JDK 8, do the following:

```
mvn clean
mvn release:clean
git clean -nx
git clean -fx
read -s GPG_PASSPHRASE
mvn -Prelease -Dgpg.passphrase=${GPG_PASSPHRASE} release:prepare
mvn -Prelease -Dgpg.passphrase=${GPG_PASSPHRASE} release:perform
```

Then go to [Sonatype](https://oss.sonatype.org/#stagingRepositories),
log in, close the repository, and release.

Announce the release on dev@calcite.apache.org.
