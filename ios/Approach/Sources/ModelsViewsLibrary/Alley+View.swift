import AssetsLibrary
import ModelsLibrary
import SwiftUI
import ViewsLibrary

extension Alley {
	public struct View: SwiftUI.View {
		let alley: Alley.Summary

		public init(alley: Alley.Summary) {
			self.alley = alley
		}

		var hasSomeBadge: Bool {
			alley.material != nil ||
				alley.mechanism != nil ||
				alley.pinBase != nil ||
				alley.pinFall != nil
		}

		public var body: some SwiftUI.View {
			VStack(alignment: .leading, spacing: .smallSpacing) {
				Text(alley.name)

				if hasSomeBadge {
					HStack {
						if let material = alley.material {
							BadgeView(
								String(describing: material),
								style: .init(foreground: .appAlleyMaterialBorder, background: .appAlleyMaterialBackground)
							)
						}
						if let mechanism = alley.mechanism {
							BadgeView(
								String(describing: mechanism),
								style: .init(foreground: .appAlleyMechanismBorder, background: .appAlleyMechanismBackground)
							)
						}
						if let pinFall = alley.pinFall {
							BadgeView(
								String(describing: pinFall),
								style: .init(foreground: .appAlleyPinFallBorder, background: .appAlleyPinFallBackground)
							)
						}
						if let pinBase = alley.pinBase {
							BadgeView(
								String(describing: pinBase),
								style: .init(foreground: .appAlleyPinBaseBorder, background: .appAlleyPinBaseBackground)
							)
						}
					}
				}
			}
		}
	}
}
