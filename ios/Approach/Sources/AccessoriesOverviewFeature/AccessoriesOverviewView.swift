import Algorithms
import AlleyEditorFeature
import AlleysListFeature
import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ErrorsFeature
import ExtensionsLibrary
import GearEditorFeature
import GearListFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
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

	public init(store: StoreOf<AccessoriesOverview>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					if viewStore.alleys.isEmpty {
						Text(Strings.Alley.Error.Empty.message)
					} else {
						ForEach(viewStore.alleys) { alley in
							Alley.View(alley)
								.swipeActions(allowsFullSwipe: true) {
									EditButton { viewStore.send(.didSwipe(.edit, .alley(alley))) }
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
				} footer: {
					if viewStore.alleys.count >= AccessoriesOverview.recentAlleysLimit {
						Text(Strings.Accessory.Overview.showingLimit(AccessoriesOverview.recentAlleysLimit))
					}
				}

				Section {
					Grid(horizontalSpacing: 0, verticalSpacing: 0) {
						ForEach(ViewState.gearKinds) { row in
							GridRow {
								ForEach(row.group) { kind in
									Button { viewStore.send(.didTapGearKind(kind)) } label: {
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
							Gear.ViewWithAvatar(gear)
								.swipeActions(allowsFullSwipe: true) {
									EditButton { viewStore.send(.didSwipe(.edit, .gear(gear))) }
								}
						}
					}
				} footer: {
					if viewStore.gear.count >= AccessoriesOverview.recentGearLimit {
						Text(Strings.Accessory.Overview.showingLimit(AccessoriesOverview.recentGearLimit))
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
						Image(systemSymbol: .plus)
					}
				}
			}
			.onAppear { viewStore.send(.onAppear) }
			.task { await viewStore.send(.task).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.gearList(store.scope(state: \.$destination.gearList, action: \.internal.destination.gearList))
		.alleysList(store.scope(state: \.$destination.alleysList, action: \.internal.destination.alleysList))
		.alleyEditor(store.scope(state: \.$destination.alleyEditor, action: \.internal.destination.alleyEditor))
		.gearEditor(store.scope(state: \.$destination.gearEditor, action: \.internal.destination.gearEditor))
	}
}

@MainActor extension View {
	fileprivate func gearList(_ store: PresentationStoreOf<GearList>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<GearList>) in
			GearListView(store: store)
		}
	}

	fileprivate func alleysList(_ store: PresentationStoreOf<AlleysList>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<AlleysList>) in
			AlleysListView(store: store)
		}
	}

	fileprivate func alleyEditor(_ store: PresentationStoreOf<AlleyEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<AlleyEditor>) in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
	}

	fileprivate func gearEditor(_ store: PresentationStoreOf<GearEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<GearEditor>) in
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
