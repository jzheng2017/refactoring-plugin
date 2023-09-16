package nl.jiankai.refactoringplugin.refactoring.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.intellij.openapi.diagnostic.Logger;
import nl.jiankai.refactoringplugin.refactoring.RefactoringData;
import nl.jiankai.refactoringplugin.refactoring.RefactoringType;

import java.util.List;
import java.util.Objects;

public class JavaParserUtil {
    private static final Logger LOGGER = Logger.getInstance(JavaParserUtil.class);
    public static List<MethodCallExpr> getMethodUsages(CompilationUnit compilationUnit, String qualifiedName) {
        return compilationUnit.findAll(MethodCallExpr.class, methodCall -> {
            try {
                return Objects.equals(qualifiedName, methodCall.resolve().getQualifiedSignature());
            } catch (Exception ex) {
                LOGGER.warn(ex);
                return false;
            }
        });
    }

    public static boolean isBreakingChange(MethodCallExpr methodCallExpr, RefactoringData refactoringData) {
        RefactoringType refactoringType = refactoringData.refactoringType();

        return switch (refactoringType) {
            case METHOD_NAME -> true;
            case METHOD_SIGNATURE -> computeMethodSignatureIsBreaking(methodCallExpr);
            default -> false;
        };
    }

    private static boolean computeMethodSignatureIsBreaking(MethodCallExpr methodCallExpr) {
        return false;
    }
}
