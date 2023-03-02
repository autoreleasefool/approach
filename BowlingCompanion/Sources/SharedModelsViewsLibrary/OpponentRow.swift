import SharedModelsLibrary
import SwiftUI

public struct OpponentRow: View {
	let opponent: Opponent

	public init(opponent: Opponent) {
		self.opponent = opponent
	}

	public var body: some View {
		Text(opponent.name)
	}
}

#if DEBUG
struct OpponentRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				OpponentRow(opponent: .init(id: .init(), name: "Joseph"))
				OpponentRow(opponent: .init(id: .init(), name: "Sarah"))
				OpponentRow(opponent: .init(id: .init(), name: "Audriana Roque"))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
