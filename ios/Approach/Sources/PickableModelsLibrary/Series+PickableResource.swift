import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension Series.Summary: PickableResource {
	public static func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Series.title : Strings.Series.List.title
	}
}
