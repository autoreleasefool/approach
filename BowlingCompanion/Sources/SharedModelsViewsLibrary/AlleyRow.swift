import SharedModelsLibrary
import SwiftUI
import AssetsLibrary
import ViewsLibrary

public struct AlleyRow: View {
	let alley: Alley
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(alley: Alley, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.alley = alley
		self.onEdit = onEdit
		self.onDelete = onDelete
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
				.frame(maxWidth: .infinity, alignment: .leading)

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
		.swipeActions(allowsFullSwipe: true) {
			if let onEdit {
				EditButton(perform: onEdit)
			}

			if let onDelete {
				DeleteButton(perform: onDelete)
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
