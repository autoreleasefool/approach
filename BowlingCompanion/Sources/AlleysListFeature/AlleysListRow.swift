import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct AlleysListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<AlleysListView.ViewState, AlleysListView.ViewAction>

	let viewStore: ViewStore
	let alley: Alley

	init(viewStore: ViewStore, alley: Alley) {
		self.viewStore = viewStore
		self.alley = alley
	}

	var body: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			Text(alley.name)
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
		.swipeActions(allowsFullSwipe: true) {
			EditButton { viewStore.send(.swipeAction(alley, .edit)) }
			DeleteButton { viewStore.send(.swipeAction(alley, .delete)) }
		}
	}
}

#if DEBUG
struct AlleysListRowPreview: PreviewProvider {
	static var previews: some View {
		WithViewStore(
			.init(
				initialState: .init(),
				reducer: AlleysList()
			),
			observe: AlleysListView.ViewState.init,
			send: AlleysList.Action.init
		) { viewStore in
			List {
				AlleysListRow(
					viewStore: viewStore,
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
}
#endif
