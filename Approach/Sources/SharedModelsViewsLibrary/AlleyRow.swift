import AssetsLibrary
import SharedModelsLibrary
import SwiftUI
import ViewsLibrary

public struct AlleyRow: View {
	let alley: Alley

	public init(alley: Alley) {
		self.alley = alley
	}

	var hasSomeBadge: Bool {
		alley.material != .unknown ||
			alley.mechanism != .unknown ||
			alley.pinBase != .unknown ||
			alley.pinFall != .unknown
	}

	public var body: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			Text(alley.name)

			if hasSomeBadge {
				HStack {
					if alley.material != .unknown {
						BadgeView(
							String(describing: alley.material),
							style: .custom(foreground: .appAlleyMaterialBorder, background: .appAlleyMaterialBackground)
						)
					}
					if alley.mechanism != .unknown {
						BadgeView(
							String(describing: alley.mechanism),
							style: .custom(foreground: .appAlleyMechanismBorder, background: .appAlleyMechanismBackground)
						)
					}
					if alley.pinFall != .unknown {
						BadgeView(
							String(describing: alley.pinFall),
							style: .custom(foreground: .appAlleyPinFallBorder, background: .appAlleyPinFallBackground)
						)
					}
					if alley.pinBase != .unknown {
						BadgeView(
							String(describing: alley.pinBase),
							style: .custom(foreground: .appAlleyPinBaseBorder, background: .appAlleyPinBaseBackground)
						)
					}
				}
			}
		}
	}
}

#if DEBUG
struct AlleyRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			AlleyRow(
				alley: .init(
					id: UUID(),
					name: "Skyview Lanes",
					address: nil,
					material: .wood,
					pinFall: .freefall,
					mechanism: .dedicated,
					pinBase: .black
				)
			)
		}
	}
}
#endif
