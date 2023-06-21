import Algorithms
import AlleyEditorFeature
import AlleysListFeature
import AssetsLibrary
import ComposableArchitecture
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

	struct ViewState: Equatable {
		let alleys: IdentifiedArrayOf<Alley.Summary>
		let gear: IdentifiedArrayOf<Gear.Summary>
		static let gearKinds = Gear.Kind.allCases.chunks(ofCount: 2).map { GearKindGroup(group: Array($0)) }

		init(state: AccessoriesOverview.State) {
			self.alleys = state.recentAlleys
			self.gear = state.recentGear
		}
	}

	enum ViewAction {
		case didObserveData
		case didTapViewAllAlleys
		case didTapViewAllGear
		case didTapAddAlley
		case didTapAddGear
		case didTapGearKind(Gear.Kind)
		case didSwipeAlley(AccessoriesOverview.SwipeAction, Alley.ID)
		case didSwipeGear(AccessoriesOverview.SwipeAction, Gear.ID)
	}

	public init(store: StoreOf<AccessoriesOverview>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AccessoriesOverview.Action.init) { viewStore in
			List {
				Section {
					if viewStore.alleys.isEmpty {
						Text(Strings.Alley.Error.Empty.message)
					} else {
						// TODO: show empty state for no alleys
						ForEach(viewStore.alleys) { alley in
							Alley.View(alley: alley)
								.swipeActions(allowsFullSwipe: true) {
									EditButton { viewStore.send(.didSwipeAlley(.edit, alley.id)) }
									DeleteButton { viewStore.send(.didSwipeAlley(.delete, alley.id)) }
								}
						}
					}
				} header: {
					HStack(alignment: .firstTextBaseline) {
						Text(Strings.Alley.List.title)
						Spacer()
						Button { viewStore.send(.didTapViewAllAlleys) } label: {
							Text(Strings.Action.viewAll)
								.font(.caption)
						}
					}
				}

				Section {
					Grid(horizontalSpacing: 0, verticalSpacing: 0) {
						ForEach(ViewState.gearKinds) { row in
							GridRow {
								ForEach(row.group) { kind in
									Button { viewStore.send(.didTapGearKind(kind)) } label: {
										HStack {
											Image(systemName: kind.systemImage)
												.resizable()
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
						Button { viewStore.send(.didTapViewAllGear) } label: {
							Text(Strings.Action.viewAll)
								.font(.caption)
						}
					}
				}

				Section {
					if viewStore.gear.isEmpty {
						Text(Strings.Gear.Error.Empty.message)
					} else {
						ForEach(viewStore.gear) { gear in
							Gear.View(gear: gear)
								.swipeActions(allowsFullSwipe: true) {
									EditButton { viewStore.send(.didSwipeGear(.edit, gear.id)) }
									DeleteButton { viewStore.send(.didSwipeGear(.delete, gear.id)) }
								}
						}
					}
				}
			}
			.navigationBarTitle(Strings.Accessory.Overview.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Menu {
						Button(Strings.Alley.List.add) { viewStore.send(.didTapAddAlley) }
						Button(Strings.Gear.List.add) { viewStore.send(.didTapAddGear) }
					} label: {
						Image(systemName: "plus")
					}
				}
			}
			.task { await viewStore.send(.didObserveData).finish() }
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

extension AccessoriesOverview.Action {
	init(action: AccessoriesOverviewView.ViewAction) {
		switch action {
		case .didObserveData:
			self = .view(.didObserveData)
		case .didTapViewAllGear:
			self = .view(.didTapViewAllGear)
		case .didTapViewAllAlleys:
			self = .view(.didTapViewAllAlleys)
		case let .didTapGearKind(kind):
			self = .view(.didTapGearKind(kind))
		case .didTapAddGear:
			self = .view(.didTapAddGear)
		case .didTapAddAlley:
			self = .view(.didTapAddAlley)
		case let .didSwipeGear(action, gear):
			self = .view(.didSwipeGear(action, gear))
		case let .didSwipeAlley(action, alley):
			self = .view(.didSwipeAlley(action, alley))
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
