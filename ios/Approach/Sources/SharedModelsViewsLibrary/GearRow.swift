import SharedModelsLibrary
import SwiftUI

public struct GearRow: View {
	let gear: Gear

	public init(gear: Gear) {
		self.gear = gear
	}

	public var body: some View {
		Label(gear.name, systemImage: gear.kind.image)
	}
}

#if DEBUG
struct GearRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				GearRow(gear: .init(bowler: .init(), id: .init(), name: "Yellow", kind: .bowlingBall))
				GearRow(gear: .init(bowler: .init(), id: .init(), name: "Bowling School, 2022", kind: .towel))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
