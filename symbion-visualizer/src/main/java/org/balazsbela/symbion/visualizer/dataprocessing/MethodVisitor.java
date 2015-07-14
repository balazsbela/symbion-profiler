package org.balazsbela.symbion.visualizer.dataprocessing;

import java.util.ArrayList;
import java.util.List;

import org.balazsbela.symbion.visualizer.models.SourceFileMethod;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes. 
 */
class MethodVisitor extends VoidVisitorAdapter {
	public List<SourceFileMethod> methods = new ArrayList<SourceFileMethod>();
    @Override
    public void visit(MethodDeclaration n, Object arg) {
        // here you can access the attributes of the method.
        // this method will be called for all methods in this 
        // CompilationUnit, including inner class methods
    	SourceFileMethod sfm = new SourceFileMethod();
    	sfm.setMethodName(n.getName());
    	sfm.setStartLine(n.getBeginLine());
    	sfm.setEndLine(n.getEndLine());
    	methods.add(sfm);
    }
	public List<SourceFileMethod> getMethods() {
		return methods;
	}
}
