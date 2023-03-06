import SharedModelsLibrary
import SwiftUI

public struct TeamRow: View {
	let team: Team

	public init(team: Team) {
		self.team = team
	}

	public var body: some View {
		Text(team.name)
	}
}

#if DEBUG
struct TeamRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				TeamRow(team: .init(id: .init(), name: "Junior Boys, 2022"))
				TeamRow(team: .init(id: .init(), name: "The Family"))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
