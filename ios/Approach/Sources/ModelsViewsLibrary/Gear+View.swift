import ModelsLibrary
import SwiftUI

extension Gear {
	public struct View: SwiftUI.View {
		let gear: Gear.Summary

		public init(gear: Gear.Summary) {
			self.gear = gear
		}

		public var body: some SwiftUI.View {
			Label(gear.name, systemImage: gear.kind.systemImage)
		}
	}
}

extension Gear.Kind {
	public var systemImage: String {
		switch self {
		case .bowlingBall: return "poweroutlet.type.h"
		case .shoes: return "shoeprints.fill"
		case .towel: return "square.split.bottomrightquarter"
		case .other: return "questionmark.app"
		}
	}
}
