// FREEHEP
package com.github.maven_nar.cpptasks.intel;
import com.github.maven_nar.cpptasks.compiler.LinkType;
import com.github.maven_nar.cpptasks.compiler.Linker;
import com.github.maven_nar.cpptasks.gcc.AbstractLdLinker;
import com.github.maven_nar.cpptasks.gcc.GccLibrarian;

public final class IntelLinux64CLinker extends AbstractLdLinker {
    private static final String[] discardFiles = new String[0];
    private static final String[] libtoolObjFiles = new String[]{".fo", ".a",
            ".lib", ".dll", ".so", ".sl"};
    private static final String[] objFiles = new String[]{".o", ".a", ".lib",
            ".dll", ".so", ".sl"};
    private static final IntelLinux64CLinker dllLinker = new IntelLinux64CLinker(
            "lib", ".so", false, new IntelLinux64CLinker("lib", ".so", true,
                    null));
    private static final IntelLinux64CLinker instance = new IntelLinux64CLinker(
            "", "", false, null);
    public static IntelLinux64CLinker getInstance() {
        return instance;
    }
    private IntelLinux64CLinker(String outputPrefix, String outputSuffix,
            boolean isLibtool, IntelLinux64CLinker libtoolLinker) {
        super("ecc", "-V", objFiles, discardFiles, outputPrefix, outputSuffix,
                isLibtool, libtoolLinker);
    }
    public Linker getLinker(LinkType type) {
        if (type.isStaticLibrary()) {
            return GccLibrarian.getInstance();
        }
        if (type.isSharedLibrary()) {
            return dllLinker;
        }
        return instance;
    }
}
