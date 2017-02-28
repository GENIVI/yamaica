### yamaica

##### Copyright
Copyright (C) 2017 Bayerische Motoren Werke Aktiengesellschaft (BMW AG).

This file is part of GENIVI Project yamaica.

##### License
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0/.

##### Version and Dependencies

==This is a DRAFT version! Next official release will be yamaica24.==

Dependencies:

- Enterprise Architect 11
- Java 8
- FrancaIDL 0.9.1
- CommonAPI 3.1.10
- Eclipse Mars

##### Build Instructions for Linux

You can build yamaica by calling maven from the command-line. Open a console in the project directory of yamaica. Then call:

```bash
mvn -f yamaica/releng/de.bmw.yamaica.releng/pom.xml -X -e --activate-profiles genivi -Dea.repo.name=yamaica-ea -Dtarget.id=de.bmw.yamaica.genivi.target clean verify

```

Make sure that the file eaapi.jar that should be shipped with yoir Enterprise Architect installation is copied to an appropriate place, e.g. `/usr/lib/jvm/java-8-openjdk-i386/jre/lib/ext/`

After the successful build you will find the yamaica updatesite in `yamaica/releng/de.bmw.yamaica.genivi.updatesite/target`.

##### Install

Download Eclipse and install the yamaica update-site via the standard Eclipse upate-site mechanism.

##### Getting started

- Start Eclipse.
- Start the project wizard with File->New->Project and select yamaica project.
- Enter Project Name.
- A project is created which contents the file `yamaica.xml` and two subfolders (gen and work).
- Open yamaica.xml by double-click and press import button.
- Now you can start the EA to Franca transformation by selecting "Franca IDL files from Enterprise Architect Project". 






