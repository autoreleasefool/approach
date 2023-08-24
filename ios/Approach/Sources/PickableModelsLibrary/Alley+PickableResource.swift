import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Alley.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}
