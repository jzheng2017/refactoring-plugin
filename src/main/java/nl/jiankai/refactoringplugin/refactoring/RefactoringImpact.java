package nl.jiankai.refactoringplugin.refactoring;

public record RefactoringImpact(String packageLocation, String className, Position position) {

    public record Position(int columnStart, int columnEnd, int rowStart, int rowEnd){}
}