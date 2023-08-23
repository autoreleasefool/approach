import Algorithms
import AlleyEditorFeature
import AlleysListFeature
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import GearEditorFeature
import GearListFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

struct GearKindGroup: Identifiable {
	let id = UUID()
	let group: [Gear.Kind]
}

public struct AccessoriesOverviewView: View {
	let store: StoreOf<AccessoriesOverview>

	static let gearKinds = Gear.Kind.allCases.chunks(ofCount: 2).map { GearKindGroup(group: Array($0)) }

	public init(store: StoreOf<AccessoriesOverview>) {
		self.store = store
	}

	public var body: some View {
		List {
			Section {
				AlleysOverviewView(
					store: store.scope(state: \.alleysOverview, action: /AccessoriesOverview.Action.InternalAction.alleysOverview)
				)
			} header: {
				HStack(alignment: .firstTextBaseline) {
					Text(Strings.Alley.List.title)
					Spacer()
					Button { store.send(.view(.didTapViewAllAlleys)) } label: {
						Text(Strings.Action.viewAll)
							.font(.caption)
					}
				}
			}

			Section {
				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					ForEach(Self.gearKinds) { row in
						GridRow {
							ForEach(row.group) { kind in
								Button { store.send(.view(.didTapGearKind(kind))) } label: {
									HStack {
										Image(systemSymbol: kind.systemSymbol)
											.scaledToFit()
											.frame(width: .smallIcon, height: .smallIcon)
										Text(kind.pluralDescription)
											.frame(maxWidth: .infinity, alignment: .leading)
									}
									.padding()
								}
								.buttonStyle(TappableElement(Asset.Colors.Primary.default, pressed: Asset.Colors.Primary.light))
							}
						}
					}
				}
				.listRowInsets(EdgeInsets())
			} header: {
				HStack(alignment: .firstTextBaseline) {
					Text(Strings.Gear.List.title)
					Spacer()
					Button { store.send(.view(.didTapViewAllGear)) } label: {
						Text(Strings.Action.viewAll)
							.font(.caption)
					}
				}
			}

			Section {
				GearOverviewView(
					store: store.scope(state: \.gearOverview, action: /AccessoriesOverview.Action.InternalAction.gearOverview)
				)
			}
		}
		.navigationBarTitle(Strings.Accessory.Overview.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Menu {
					Button(Strings.Alley.List.add) { store.send(.view(.didTapAddAlley)) }
					Button(Strings.Gear.List.add) { store.send(.view(.didTapAddGear)) }
				} label: {
					Image(systemSymbol: .plus)
				}
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AccessoriesOverview.Destination.State.gearList,
			action: AccessoriesOverview.Destination.Action.gearList
		) { (store: StoreOf<GearList>) in
			GearListView(store: store)
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AccessoriesOverview.Destination.State.alleysList,
			action: AccessoriesOverview.Destination.Action.alleysList
		) { (store: StoreOf<AlleysList>) in
			AlleysListView(store: store)
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AccessoriesOverview.Destination.State.alleyEditor,
			action: AccessoriesOverview.Destination.Action.alleyEditor
		) { (store: StoreOf<AlleyEditor>) in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AccessoriesOverview.Destination.State.gearEditor,
			action: AccessoriesOverview.Destination.Action.gearEditor
		) { (store: StoreOf<GearEditor>) in
			NavigationStack {
				GearEditorView(store: store)
			}
		}
	}
}

extension Gear.Kind {
	var pluralDescription: String {
		switch self {
		case .shoes: return Strings.Gear.Properties.Kind.shoes
		case .bowlingBall: return Strings.Gear.Properties.Kind.bowlingBalls
		case .towel: return Strings.Gear.Properties.Kind.towels
		case .other: return Strings.other
		}
	}
}
