/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.core

import org.franca.core.franca.FBasicTypeId
import org.franca.core.franca.FType
import org.franca.core.franca.FrancaFactory

class InterfaceDefinitionModelCreator
{
    var FType titleStruct;

    def create FrancaFactory::eINSTANCE.createFModel createModel(String packageString, boolean addDemoContent)
    {
        name = packageString

        if(addDemoContent)
        {
            typeCollections.add(createTypeCollectionAudioPlayer)
            interfaces.add(createInterfaceAudioPlayer)
        }
    }

    def private create FrancaFactory::eINSTANCE.createFInterface createInterfaceAudioPlayer()
    {
        name = "AudioPlayer"
        version = getVersion(1, 5)

        methods.add(createMethodPlay)
        methods.add(createMethodPause)
    }

    def private create FrancaFactory::eINSTANCE.createFMethod createMethodPlay()
    {
        name = "play"

        inArgs.add(createStartTimeArgument())
        inArgs.add(createTitleArgument())
    }

    def private create FrancaFactory::eINSTANCE.createFArgument createStartTimeArgument()
    {
        name = "startTime"
        type = FBasicTypeId::INT32_VALUE.getTypeRef
    }

    def private create FrancaFactory::eINSTANCE.createFArgument createTitleArgument()
    {
        name = "title"

        val typeRef = FrancaFactory::eINSTANCE.createFTypeRef
        typeRef.derived = titleStruct

        type = typeRef
    }

    def private create FrancaFactory::eINSTANCE.createFMethod createMethodPause()
    {
        name = "stop"
        fireAndForget = true
    }

    def private create FrancaFactory::eINSTANCE.createFTypeCollection createTypeCollectionAudioPlayer()
    {
        name = "AudioPlayerTypeCollection"
        version = getVersion(2, 3)

        types.add(createTitleStruct())
    }

    def private create FrancaFactory::eINSTANCE.createFStructType createTitleStruct()
    {
        titleStruct = it
        name = "Title"

        elements.add(createSourceField())
    }

    def private create FrancaFactory::eINSTANCE.createFField createSourceField()
    {
        name = "src"
        type = FBasicTypeId::STRING_VALUE.getTypeRef
    }

    def private getTypeRef(int typeId)
    {
        val typeRef = FrancaFactory::eINSTANCE.createFTypeRef
        typeRef.predefined = FBasicTypeId::get(typeId)
        typeRef
    }

    def private getVersion(int minor, int major)
    {
        val interfaceVersion = FrancaFactory::eINSTANCE.createFVersion
        interfaceVersion.major = minor
        interfaceVersion.minor = major
        interfaceVersion
    }
}
