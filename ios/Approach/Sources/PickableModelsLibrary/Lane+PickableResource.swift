import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Lane.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Lane.title : Strings.Lane.List.title
	}
}
