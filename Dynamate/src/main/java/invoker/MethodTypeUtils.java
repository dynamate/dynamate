package invoker;

public class MethodTypeUtils {
	public static String fromString(String typeDeclarationString) {

		StringBuilder sb = new StringBuilder();
		String[] types = typeDeclarationString.split(",\\s*");

		for (String type : types) {
			String[] split = type.split("\\s+");
			String justType = split[0].replaceAll("<\\w+>", "");
			sb.append(justType).append(".class, ");
		}

		if (sb.length() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println("Collection<Integer>".replaceAll("<\\w+>", ""));
	}
}
