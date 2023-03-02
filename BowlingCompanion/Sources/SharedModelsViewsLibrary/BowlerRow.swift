import SharedModelsLibrary
import SwiftUI

public struct BowlerRow: View {
	let bowler: Bowler

	public init(bowler: Bowler) {
		self.bowler = bowler
	}

	public var body: some View {
		AvatarLabelView(bowler.avatar, size: .medium, title: bowler.name)
	}
}

#if DEBUG
struct BowlerRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				BowlerRow(bowler: .init(id: .init(), name: "Joseph", avatar: .text("JR", .random())))
				BowlerRow(bowler: .init(id: .init(), name: "Sarah", avatar: .text("SA", .random())))
				BowlerRow(bowler: .init(id: .init(), name: "Audriana Roque", avatar: .text("AR", .random())))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
