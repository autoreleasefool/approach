import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension League.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.League.title : Strings.League.List.title
	}
}
