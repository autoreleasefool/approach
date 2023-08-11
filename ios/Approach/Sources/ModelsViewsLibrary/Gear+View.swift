import AssetsLibrary
import ModelsLibrary
import SwiftUI

extension Gear {
	public struct View: SwiftUI.View {
		let gear: Gear.Summary

		public init(gear: Gear.Summary) {
			self.gear = gear
		}

		public var body: some SwiftUI.View {
			Label(gear.name, systemSymbol: gear.kind.systemSymbol)
		}
	}
}

extension Gear.Kind {
	public var systemSymbol: SFSymbol {
		switch self {
		case .bowlingBall: return .poweroutletTypeH
		case .shoes: return .shoeprintsFill
		case .towel: return .squareSplitBottomrightquarter
		case .other: return .questionmarkApp
		}
	}
}
