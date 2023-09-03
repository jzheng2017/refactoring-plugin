package nl.jiankai.refactoringplugin.refactoring;

public record RefactoringData(String packageLocation, String elementName, String fullyQualifiedSignature, RefactoringType refactoringType) {
}
