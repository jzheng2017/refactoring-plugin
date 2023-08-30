package nl.jiankai.refactoringplugin.refactoring.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.intellij.openapi.diagnostic.Logger;

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

    public static List<VariableDeclarationExpr> getVariableDeclarations(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(VariableDeclarationExpr.class, decl -> {
            return true;
        });
    }
}
