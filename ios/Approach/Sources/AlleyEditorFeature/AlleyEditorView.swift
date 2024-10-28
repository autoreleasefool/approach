import AddressLookupFeature
import AlleysRepositoryInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import MapKit
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: AlleyEditor.self)
public struct AlleyEditorView: View {
	@Bindable public var store: StoreOf<AlleyEditor>

	public init(store: StoreOf<AlleyEditor>) {
		self.store = store
	}

	public var body: some View {
		FormView(store: store.scope(state: \.form, action: \.internal.form)) {
			detailsSection
			mapSection
			materialSection
			mechanismSection
			pinFallSection
			pinBaseSection
			lanesSection
			Banner(.message(Strings.Alley.Editor.Help.askAStaffMember), style: .plain)
				.listRowInsets(EdgeInsets())
		}
		.onAppear { send(.onAppear) }
		.alleyLanes($store.scope(state: \.destination?.alleyLanes, action: \.internal.destination.alleyLanes))
		.addressLookup($store.scope(state: \.destination?.addressLookup, action: \.internal.destination.addressLookup))
	}

	private var detailsSection: some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: $store.name
			)
			HStack {
				Button { send(.didTapAddressField) } label: {
					Text(store.location?.title ?? Strings.Address.title)
				}
				Spacer()
				if store.location != nil {
					Image(systemSymbol: .xCircleFill)
				}
			}
		}
	}

	@ViewBuilder private var mapSection: some View {
		if let location = store.location {
			Section {
				Map(position: $store.mapPosition, interactionModes: []) {
					Marker(location.title, coordinate: location.coordinate.mapCoordinate)
						.tint(Asset.Colors.Action.default.swiftUIColor)
				}
				.frame(maxWidth: .infinity)
				.frame(height: 125)
				.padding(0)
			}
			.listRowInsets(EdgeInsets())
		}
	}

	private var materialSection: some View {
		Section {
			Picker(
				Strings.Alley.Properties.material,
				selection: $store.material
			) {
				Text("").tag(nil as Alley.Material?)
				ForEach(Alley.Material.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.Material.help)
		}
	}

	private var pinFallSection: some View {
		Section {
			Picker(
				Strings.Alley.Properties.pinFall,
				selection: $store.pinFall
			) {
				Text("").tag(nil as Alley.PinFall?)
				ForEach(Alley.PinFall.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.PinFall.help)
		}
	}

	private var mechanismSection: some View {
		Section {
			Picker(
				Strings.Alley.Properties.mechanism,
				selection: $store.mechanism
			) {
				Text("").tag(nil as Alley.Mechanism?)
				ForEach(Alley.Mechanism.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.Mechanism.help)
		}
	}

	private var pinBaseSection: some View {
		Section {
			Picker(
				Strings.Alley.Properties.pinBase,
				selection: $store.pinBase
			) {
				Text("").tag(nil as Alley.PinBase?)
				ForEach(Alley.PinBase.allCases) {
					Text(String(describing: $0)).tag(Optional($0))
				}
			}
		} footer: {
			Text(Strings.Alley.Editor.Fields.PinBase.help)
		}
	}

	private var lanesSection: some View {
		Section {
			if store.newLanes.isEmpty && store.existingLanes.isEmpty {
				Text(Strings.Alley.Properties.Lanes.none)
			} else {
				ForEach(store.existingLanes) { lane in
					Lane.View(label: lane.label, position: lane.position)
				}
				ForEach(store.newLanes) { lane in
					Lane.View(label: lane.label, position: lane.position)
				}
			}
		} header: {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Lane.List.title)
				Spacer()
				Button { send(.didTapManageLanes) } label: {
					Text(Strings.Action.manage)
						.font(.caption)
				}
			}
		}
	}
}

extension View {
	fileprivate func alleyLanes(_ store: Binding<StoreOf<AlleyLanesEditor>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<AlleyLanesEditor>) in
			AlleyLanesEditorView(store: store)
		}
	}

	fileprivate func addressLookup(_ store: Binding<StoreOf<AddressLookup>?>) -> some View {
		sheet(item: store) { (store: StoreOf<AddressLookup>) in
			NavigationStack {
				AddressLookupView(store: store)
					.navigationTitle(Strings.Alley.Editor.Fields.Address.editorTitle)
					.navigationBarTitleDisplayMode(.inline)
			}
		}
	}
}
