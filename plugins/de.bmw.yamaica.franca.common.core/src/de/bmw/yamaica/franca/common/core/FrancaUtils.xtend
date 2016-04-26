/* Copyright (C) 2013-2015 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.franca.common.core

import java.math.BigInteger
import java.text.Normalizer
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.core.runtime.Assert
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.Path
import org.eclipse.emf.ecore.EObject
import org.franca.core.franca.FExpression
import org.franca.core.franca.FModel
import org.franca.core.franca.FModelElement
import org.franca.core.franca.FrancaFactory
import org.franca.core.utils.ExpressionEvaluator

class FrancaUtils
{
    private static val LOGGER = Logger.getLogger(typeof(FrancaUtils).name);

    protected static val SPACES_ERROR = "The string \"%s\" contains spaces!"
    protected static val LEADING_NUMBERS_ERROR = "The string \"%s\" may not begin with a number!"
    protected static val SPECIAL_CHARS_ERROR = "The string \"%s\" may only contain characters of the group [a-zA-Z0-9_]!"
    protected static val KEYWORDS_ERROR = "The string \"%s\" is a Franca keyword!"
    protected static val ZERO_LENGTH_ERROR = "The string \"%s\" has length zero after normalization!"
    protected static val POINT_BEGIN_ERROR = "The string \"%s\" may not begin with a \".\"!"
    protected static val POINT_END_ERROR = "The string \"%s\" may not end with a \".\"!"

    public static val INTERFACE_DEFINITION_FILE_EXTENSION = YamaicaFrancaConstants.FIDL
    public static val DEPLOYMENT_SPECIFICATION_FILE_EXTENSION = YamaicaFrancaConstants.FDEPL
    public static val DEPLOYMENT_DEFINITION_FILE_EXTENSION = YamaicaFrancaConstants.FDEPL
    public static val PACKAGE_SEPARATOR = "."

    public static val SPACES = 0x01
    public static val LEADING_NUMBERS = 0x02
    public static val SPECIAL_CHARS = 0x04
    public static val INTERFACE_DEFINITION_KEYWORDS = 0x08
    public static val DEPLOYMENT_DEFINITION_KEYWORDS = 0x10
    public static val KEYWORDS = 0x18
    public static val ZERO_LENGTH = 0x20

    // TODO to be removed.
    public static val CPP_KEYWORDS = 0x40

    public static val NONE = 0x0
    public static val ALL_WITHOUT_KEYWORDS = SPACES.bitwiseOr(LEADING_NUMBERS).bitwiseOr(SPECIAL_CHARS).bitwiseOr(ZERO_LENGTH).
        bitwiseOr(CPP_KEYWORDS)
    public static val ALL = ALL_WITHOUT_KEYWORDS.bitwiseOr(KEYWORDS)
    public static val ALL_FOR_INTERFACE_DEFINITIONS = ALL_WITHOUT_KEYWORDS.bitwiseOr(INTERFACE_DEFINITION_KEYWORDS)
    public static val ALL_FOR_DEPLOYMENT_DEFINITIONS = ALL_WITHOUT_KEYWORDS.bitwiseOr(DEPLOYMENT_DEFINITION_KEYWORDS)

    public static val keyWords = #{
        "alignas",
        "alignof",
        "and",
        "and_eq",
        "asm",
        "auto",
        "bitand",
        "bitor",
        "bool",
        "break",
        "case",
        "catch",
        "char",
        "char16_t",
        "char32_t",
        "class",
        "compl",
        "const",
        "constexpr",
        "const_cast",
        "continue",
        "decltype",
        "default",
        "delete",
        "do",
        "double",
        "dynamic_cast",
        "else",
        "enum",
        "explicit",
        "export",
        "extern",
        "false",
        "float",
        "for",
        "friend",
        "goto",
        "if",
        "inline",
        "int",
        "long",
        "mutable",
        "namespace",
        "new",
        "noexcept",
        "not",
        "not_eq",
        "nullptr",
        "operator",
        "or",
        "or_eq",
        "private",
        "protected",
        "public",
        "register",
        "reinterpret_cast",
        "return",
        "short",
        "signed",
        "sizeof",
        "static",
        "static_assert",
        "static_cast",
        "struct",
        "switch",
        "template",
        "this",
        "thread_local",
        "throw",
        "true",
        "try",
        "typedef",
        "typeid",
        "typename",
        "union",
        "unsigned",
        "using",
        "virtual",
        "void",
        "volatile",
        "wchar_t",
        "while",
        "xor",
        "xor_eq"
    }

    /**
     * Returns a normalized version of the referred string parameter.
     */
    def static String normalizeName(String name, int normalizeMask, int toleranceMask)
    {
        Assert.isNotNull(name)

        var normalizedName = name

        // Search for spaces.
        if(normalizedName.matches(".*\\s.*"))
        {
            if((normalizeMask.bitwiseAnd(SPACES)) > 0)
            {
                normalizedName = normalizedName.replaceAll("\\s", "_")
            }
            else if((toleranceMask.bitwiseAnd(SPACES)) == 0)
            {
                val String exceptionMsg = String.format(SPACES_ERROR, name)
                LOGGER.log(Level.SEVERE, exceptionMsg);
                throw new IllegalArgumentException(exceptionMsg)
            }
        }

        // Search for leading numbers.
        if(normalizedName.matches("[0-9].*"))
        {
            if((normalizeMask.bitwiseAnd(LEADING_NUMBERS)) > 0)
            {
                normalizedName = "_" + normalizedName
            }
            else if((toleranceMask.bitwiseAnd(LEADING_NUMBERS)) == 0)
            {
                val String exceptionMsg = String.format(LEADING_NUMBERS_ERROR, name)
                LOGGER.log(Level.SEVERE, exceptionMsg);
                throw new IllegalArgumentException(exceptionMsg)
            }
        }

        // Search for strings that contain characters other than "a-z", "A-Z", "0-9" or "_".
        // Do not match if the string does begin with "^".
        if(!normalizedName.matches("\\^?[a-zA-Z0-9_]+"))
        {
            if((normalizeMask.bitwiseAnd(SPECIAL_CHARS)) > 0)
            {

                // Replace German special characters.
                normalizedName = normalizedName.replace("ä", "ae")
                normalizedName = normalizedName.replace("ö", "oe")
                normalizedName = normalizedName.replace("ü", "ue")
                normalizedName = normalizedName.replace("Ä", "Ae")
                normalizedName = normalizedName.replace("Ö", "Oe")
                normalizedName = normalizedName.replace("Ü", "Ue")
                normalizedName = normalizedName.replace("ß", "ss")

                // Do unicode normalization (e.g. é => e, î => i).
                normalizedName = Normalizer.normalize(normalizedName, Normalizer.Form.NFKD)
                normalizedName = normalizedName.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+", "")

                // Replace all remaining special characters with "_".
                normalizedName = normalizedName.replaceAll("[^a-zA-Z0-9_]", "_")
            }
            else if((toleranceMask.bitwiseAnd(SPECIAL_CHARS)) == 0)
            {
                val String exceptionMsg = String.format(SPECIAL_CHARS_ERROR, name)
                LOGGER.log(Level.SEVERE, exceptionMsg);
                throw new IllegalArgumentException(exceptionMsg)
            }
        }

        // Keep in mind: Normalization of Franca keywords should be solved by Franca itself.
        // See also GLIPCI-415.
        // Check if normalization led to a string with length zero.
        if(normalizedName.length == 0)
        {
            if((normalizeMask.bitwiseAnd(ZERO_LENGTH)) > 0)
            {
                normalizedName = "_"
            }
            else if((toleranceMask.bitwiseAnd(ZERO_LENGTH)) == 0)
            {
                val String exceptionMsg = String.format(ZERO_LENGTH_ERROR, name)
                LOGGER.log(Level.SEVERE, exceptionMsg);
                throw new IllegalArgumentException(exceptionMsg)
            }
        }

        // TODO to be removed.
        if(keyWords.contains(normalizedName))
        {
            if((normalizeMask.bitwiseAnd(CPP_KEYWORDS)) > 0)
            {
                normalizedName += "_"
            }
        }

        return normalizedName
    }

    /**
     * Returns a new path where all segments have been normalized.
     */
    def static IPath normalizeNamespacePath(IPath namespace, int normalizeMask, int toleranceMask)
    {
        return new Path(namespace.segments.map[normalizeName(normalizeMask, toleranceMask)].join(IPath.SEPARATOR.toString))
    }

    /**
     * Returns a new String where all segments have been normalized.
     */
    def static String normalizeNamespaceString(String namespace, int normalizeMask, int toleranceMask)
    {
        Assert.isNotNull(namespace)
        Assert.isTrue(namespace.length > 0)

        return switch namespace
        {
            case namespace.startsWith(PACKAGE_SEPARATOR): {
                val String exceptionMsg = String.format(POINT_BEGIN_ERROR, namespace)
                LOGGER.log(Level.SEVERE, exceptionMsg);
                throw new IllegalArgumentException(exceptionMsg)
            }
            case namespace.endsWith(PACKAGE_SEPARATOR): {
                val String exceptionMsg = String.format(POINT_END_ERROR, namespace)
                LOGGER.log(Level.SEVERE, exceptionMsg);
                throw new IllegalArgumentException(exceptionMsg)
            }
            default:
                namespace.split("\\" + PACKAGE_SEPARATOR).map[normalizeName(normalizeMask, toleranceMask)].join(PACKAGE_SEPARATOR)
        }
    }

    /**
     * Returns the default relative save path of a Franca model. The name of the Franca model must be set
     * already.
     */
    def static IPath getRelativeFidlPackagePath(FModel francaModel)
    {
        val IPath path = new Path(namespace2PathString(francaModel.name))

        return normalizeNamespacePath(path, ALL_FOR_INTERFACE_DEFINITIONS, ALL).addFileExtension(INTERFACE_DEFINITION_FILE_EXTENSION)
    }

    def static String namespace2PathString(String namespaceString)
    {
        return namespaceString.replace(PACKAGE_SEPARATOR, IPath.SEPARATOR.toString)
    }

    def static String namespace2PathString(IPath namespacePath)
    {
        return namespace2PathString(namespacePath.toString)
    }

    def static String path2NamespaceString(String pathString)
    {
        return pathString.replace(IPath.SEPARATOR.toString, PACKAGE_SEPARATOR)
    }

    def static String path2NamespaceString(IPath path)
    {
        return path2NamespaceString(path.toString)
    }

    def static IPath getFullyQualifiedNameAsPath(EObject object)
    {
        return switch object
        {
            FModelElement:
            {
                val String name = object.name
                val EObject parent = object.eContainer

                if(null != parent)
                {
                    val IPath parentPath = getFullyQualifiedNameAsPath(parent)

                    if(null != name)
                    {
                        parentPath.append(name)
                    }
                    else
                    {
                        parentPath
                    }
                }
                else
                {
                    if(null != name)
                    {
                        new Path(name)
                    }
                    else
                    {
                        Path.EMPTY
                    }
                }
            }
            FModel:
            {
                val String name = namespace2PathString(object.name)

                if(null != name)
                {
                    new Path(name)
                }
                else
                {
                    Path.EMPTY
                }
            }
            default:
            {
                null
            }
        }
    }

    def static String getFullyQualifiedName(FModelElement modelElement)
    {
        return path2NamespaceString(getFullyQualifiedNameAsPath(modelElement))
    }

    def static FExpression getEnumeratorValue(String value)
    {
        return switch trimmedValue : if(value != null) value.trim else ""
        {
            case trimmedValue.length > 0:
            {
                try
                {
                    FrancaFactory.eINSTANCE.createFIntegerConstant => [
                        // Create new BigInteger in case of number strings only.
                        // Otherwise usage of Long.decode allows a simple DecimalNumeral, HexDigits as well as OctalDigits (disabled with the usage of removeLeadingZeros) to integer translation.
                        ^val = if(trimmedValue.matches("\\d+")) new BigInteger(trimmedValue) else BigInteger.valueOf(Long.decode(trimmedValue.removeLeadingZeros))
                    ]
                }
                catch(NumberFormatException e)
                {
                    LOGGER.log(Level.FINEST, String.format("NumberFormatException occurred while parsing enumeration value. Using the origin value '%s' instead.", trimmedValue))

                    FrancaFactory.eINSTANCE.createFStringConstant => [
                        ^val = trimmedValue
                    ]
                }
            }
            default:
            {
                null
            }
        }
    }

    /**
     * Removes all leading zeros to avoid usage of the octal value format (not very common).
     */
    private static def String removeLeadingZeros(String stringValue) {
        // Remove leading zero to avoid octal translation.
        if(stringValue.isOctalFormat) {
            var int index = 0;
            val char firstChar = stringValue.charAt(0);
            // Handle sign, if present
            if (Character.compare(firstChar, '-') == 0) {
                index++;
            } else if (Character.compare(firstChar, '+') == 0) {
                index++;
            }
            val int begin = index;

            // Increment index for each leading zero.
            while(stringValue.startsWith("0", index)) {
                index++;
            }

            // Remove leading zeros.
            return stringValue.substring(0, begin) + stringValue.subSequence(index, stringValue.length)
        }
        return stringValue
    }

    /**
     * Return true in case of using the octal value format (usage is not very common). Otherwise false.
     */
    private static def boolean isOctalFormat(String trimmedValue) {
        var int index = 0;
        val char firstChar = trimmedValue.charAt(0);
        // Handle sign, if present.
        if (Character.compare(firstChar, '-') == 0) {
            index++;
        }
        // WATCH OUT! Using if(firstChar == '+') {} failed surprisingly!
        else if (Character.compare(firstChar, '+') == 0) {
            index++;
        }

        // Is the hex radix specifier present?
        val boolean isHex =trimmedValue.startsWith("0x", index) || trimmedValue.startsWith("0X", index)
        // Is the octal radix specifier present?
        val boolean isOctal = trimmedValue.startsWith("0", index) && trimmedValue.length() > 1 + index

        // Return true in case of using the octal format. False otherwise.
        return !isHex && isOctal
    }

    def static String getEnumeratorValue(FExpression expression)
    {
        if (expression != null)
        {
            val intConstValue = ExpressionEvaluator::evaluateIntegerOrParseString(expression)
            if (intConstValue != null)
                return intConstValue.toString
        }
    }

    /**
     * Tries to restore origin file name using a fileNameCache map. Considers the file (last segment of the namespace) name only.
     *
     * Example 1:
     * File: 'tc_317_interfaces_versions/test_cases/test_3_interfaces/Test_Case_317_Interfaces_Versions.fidl'
     * Package: 'test_cases.test_3_interfaces.test_case_317_interfaces_versions'
     *
     * Expected value of the parameter namespace: '/test_cases/test_3_interfaces/test_case_317_interfaces_versions.fidl' (or similar)
     * The parameter fileNameCache should map the fmodel object to its origin file name: 'Test_Case_317_Interfaces_Versions'
     *
     * Therefore the result should be:'/test_cases/test_3_interfaces/Test_Case_317_Interfaces_Versions.fidl' (or similar)
     *
     * Example 2 (anonymous TypeCollection - GLIPCI-655 related):
     * File: 'tc_204_datatypes_enumerations/test_cases/test_2_datatypes/Test_Case_204_Enumerations.fidl'
     * Package: 'test_cases.test_2_datatypes'
     *
     * Expected value of the parameter namespace: '/test_cases/test_2_datatypes.fidl' (or similar)
     * The parameter fileNameCache should map the fmodel object to its origin file name: 'Test_Case_204_Enumerations'
     *
     * Therefore the result should be:'/test_cases/test_2_datatypes/Test_Case_204_Enumerations.fidl' (or similar)
     */
    def static IPath restoreOriginFileName(Map<FModel, String> fileNameCache, IPath namespace, FModel fmodel)
    {
        if((fileNameCache == null || fileNameCache.empty || !fileNameCache.containsKey(fmodel)) || (namespace == null || namespace.segmentCount < 1))
        {
            // LOGGER.log(Level.FINEST, String.format("Could not restore the origin file name of fmodel ('%s') with namespace '%s'", fmodel, namespace));
            // Should probably not happen. Origin file name could not be restored.
            // Running Franca2Fibex Tests will cause this!
            return namespace;
        }

        var withoutFileExtension = namespace.removeFileExtension
        val originFileName = fileNameCache.get(fmodel)

        // There are two different types:
        // - Anonymous TypeCollection (GLIPCI-655 related): Last package segment differs from origin file name (ignored case). Append origin file name (and file extension) only.
        // - Otherwise: Last package segment should be equals (ignored case) with origin file name. Replace with origin file name (and file extension).
        if(withoutFileExtension.lastSegment.equalsIgnoreCase(originFileName)) {
            withoutFileExtension = withoutFileExtension.removeLastSegments(1)
        }
        return withoutFileExtension.append(originFileName).addFileExtension(namespace.fileExtension);
    }
}
