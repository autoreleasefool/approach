import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct PreBowlHeader: View {
	let series: Series.GameHost

	public var body: some View {
		switch series.preBowl {
		case .regular:
			EmptyView()
		case .preBowl:
			Section(Strings.Game.List.Header.preBowl) {
				if let bowledOnDate = series.bowledOnDate {
					Text(Strings.Game.List.Header.preBowledOn(bowledOnDate.longFormat))
				} else {
					Text(Strings.Game.List.Header.unusedPreBowl)
				}
			}
		}
	}
}
