import Algorithms
import AlleyEditorFeature
import AlleysListFeature
import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ErrorsFeature
import ExtensionsPackageLibrary
import GearEditorFeature
import GearListFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

struct GearKindGroup: Identifiable {
	let id = UUID()
	let group: [Gear.Kind]

	static let groups = Gear.Kind.allCases.chunks(ofCount: 2).map { GearKindGroup(group: Array($0)) }
}

@ViewAction(for: AccessoriesOverview.self)
public struct AccessoriesOverviewView: View {
	@Bindable public var store: StoreOf<AccessoriesOverview>

	public init(store: StoreOf<AccessoriesOverview>) {
		self.store = store
	}

	public var body: some View {
		List {
			Section {
				if store.recentAlleys.isEmpty {
					Text(Strings.Alley.Error.Empty.message)
				} else {
					ForEach(store.recentAlleys) { alley in
						Alley.View(alley)
							.swipeActions(allowsFullSwipe: true) {
								EditButton { send(.didSwipe(.edit, .alley(alley))) }
							}
					}
				}
			} header: {
				HStack(alignment: .firstTextBaseline) {
					Text(Strings.Alley.List.title)
					Spacer()
					Button { send(.didTapViewAllAlleys) } label: {
						Text(Strings.Action.viewAll)
							.font(.caption)
					}
				}
			} footer: {
				if store.recentAlleys.count >= AccessoriesOverview.recentAlleysLimit {
					Text(Strings.Accessory.Overview.showingLimit(AccessoriesOverview.recentAlleysLimit))
				}
			}

			Section {
				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					ForEach(GearKindGroup.groups) { row in
						GridRow {
							ForEach(row.group) { kind in
								Button { send(.didTapGearKind(kind)) } label: {
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
					Button { send(.didTapViewAllGear) } label: {
						Text(Strings.Action.viewAll)
							.font(.caption)
					}
				}
			}

			Section {
				if store.recentGear.isEmpty {
					Text(Strings.Gear.Error.Empty.message)
				} else {
					ForEach(store.recentGear) { gear in
						Gear.ViewWithAvatar(gear)
							.swipeActions(allowsFullSwipe: true) {
								EditButton { send(.didSwipe(.edit, .gear(gear))) }
							}
					}
				}
			} footer: {
				if store.recentGear.count >= AccessoriesOverview.recentGearLimit {
					Text(Strings.Accessory.Overview.showingLimit(AccessoriesOverview.recentGearLimit))
				}
			}
		}
		.navigationBarTitle(Strings.Accessory.Overview.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Menu {
					Button(Strings.Alley.List.add) { send(.didTapAddAlley) }
					Button(Strings.Gear.List.add) { send(.didTapAddGear) }
				} label: {
					Image(systemSymbol: .plus)
				}
			}
		}
		.onAppear { send(.onAppear) }
		.task { await send(.task).finish() }
		.gearList($store.scope(state: \.destination?.gearList, action: \.internal.destination.gearList))
		.alleysList($store.scope(state: \.destination?.alleysList, action: \.internal.destination.alleysList))
		.alleyEditor($store.scope(state: \.destination?.alleyEditor, action: \.internal.destination.alleyEditor))
		.gearEditor($store.scope(state: \.destination?.gearEditor, action: \.internal.destination.gearEditor))
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.alert($store.scope(state: \.destination?.alert, action: \.internal.destination.alert))
	}
}

@MainActor extension View {
	fileprivate func gearList(_ store: Binding<StoreOf<GearList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<GearList>) in
			GearListView(store: store)
		}
	}

	fileprivate func alleysList(_ store: Binding<StoreOf<AlleysList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<AlleysList>) in
			AlleysListView(store: store)
		}
	}

	fileprivate func alleyEditor(_ store: Binding<StoreOf<AlleyEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<AlleyEditor>) in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
	}

	fileprivate func gearEditor(_ store: Binding<StoreOf<GearEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<GearEditor>) in
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
