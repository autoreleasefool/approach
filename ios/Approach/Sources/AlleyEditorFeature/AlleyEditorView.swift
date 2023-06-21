import AddressLookupFeature
import AlleysRepositoryInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import MapKit
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct AlleyEditorView: View {
	let store: StoreOf<AlleyEditor>

	struct ViewState: Equatable {
		@BindingState public var name: String
		@BindingState public var material: Alley.Material?
		@BindingState public var pinFall: Alley.PinFall?
		@BindingState public var mechanism: Alley.Mechanism?
		@BindingState public var pinBase: Alley.PinBase?
		public let location: Location.Edit?

		let hasLanesEnabled: Bool
		let newLanes: IdentifiedArrayOf<Lane.Create>
		let existingLanes: IdentifiedArrayOf<Lane.Edit>

		init(state: AlleyEditor.State) {
			self.name = state.name
			self.material = state.material
			self.pinFall = state.pinFall
			self.mechanism = state.mechanism
			self.pinBase = state.pinBase
			self.location = state.location
			self.hasLanesEnabled = state.hasLanesEnabled
			self.newLanes = state.newLanes
			self.existingLanes = state.existingLanes
		}
	}

	enum ViewAction: BindableAction {
		case didTapAddressField
		case didTapRemoveAddressButton
		case didTapManageLanes
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<AlleyEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyEditor.Action.init) { viewStore in
			FormView(store: store.scope(state: \.form, action: /AlleyEditor.Action.InternalAction.form)) {
				detailsSection(viewStore)
				mapSection(viewStore)
				materialSection(viewStore)
				mechanismSection(viewStore)
				pinFallSection(viewStore)
				pinBaseSection(viewStore)
				if viewStore.hasLanesEnabled {
					lanesSection(viewStore)
				}
				Banner(.message(Strings.Alley.Editor.Help.askAStaffMember))
					.listRowInsets(EdgeInsets())
			}
		}
		.sheet(store: store.scope(state: \.$addressLookup, action: { .internal(.addressLookup($0)) })) { scopedStore in
			NavigationStack {
				AddressLookupView(store: scopedStore)
					.navigationTitle(Strings.Alley.Editor.Fields.Address.editorTitle)
					.navigationBarTitleDisplayMode(.inline)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$alleyLanesEditor, action: { .internal(.alleyLanesEditor($0)) })
		) {
			AlleyLanesEditorView(store: $0)
		}
	}

	private func detailsSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: viewStore.binding(\.$name)
			)
			HStack {
				Button { viewStore.send(.didTapAddressField) } label: {
					Text(viewStore.location?.title ?? "Address")
				}
				Spacer()
				if viewStore.location != nil {
					Button { viewStore.send(.didTapRemoveAddressButton) } label: {
						Image(systemName: "x.circle.fill")
					}
				}
			}
		}
	}

	@ViewBuilder private func mapSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		if let location = viewStore.location {
			Section {
				Map(
					coordinateRegion: .constant(.init(
						center: location.coordinate.mapCoordinate,
						span: .init(latitudeDelta: 0.005, longitudeDelta: 0.005)
					)),
					interactionModes: [],
					annotationItems: [location]
				) { place in
					MapMarker(coordinate: place.coordinate.mapCoordinate, tint: Asset.Colors.Action.default.swiftUIColor)
				}
				.frame(maxWidth: .infinity)
				.frame(height: 125)
				.padding(0)
			}
			.listRowInsets(EdgeInsets())
		}
	}

	private func materialSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.material,
				selection: viewStore.binding(\.$material)
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

	private func pinFallSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.pinFall,
				selection: viewStore.binding(\.$pinFall)
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

	private func mechanismSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.mechanism,
				selection: viewStore.binding(\.$mechanism)
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

	private func pinBaseSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Picker(
				Strings.Alley.Properties.pinBase,
				selection: viewStore.binding(\.$pinBase)
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

	private func lanesSection(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		Section {
			Group {
				if viewStore.newLanes.isEmpty && viewStore.existingLanes.isEmpty {
					Text(Strings.Alley.Properties.Lanes.none)
				} else {
					ForEach(viewStore.existingLanes) { lane in
						Lane.View(label: lane.label, position: lane.position)
					}
					ForEach(viewStore.newLanes) { lane in
						Lane.View(label: lane.label, position: lane.position)
					}
				}
			}
		} header: {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Lane.List.title)
				Spacer()
				Button { viewStore.send(.didTapManageLanes) } label: {
					Text(Strings.Action.manage)
						.font(.caption)
				}
			}
		}
	}
}

extension AlleyEditor.State {
	var view: AlleyEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.name = newValue.name
			self.material = newValue.material
			self.pinFall = newValue.pinFall
			self.mechanism = newValue.mechanism
			self.pinBase = newValue.pinBase
		}
	}
}

extension AlleyEditor.Action {
	init(action: AlleyEditorView.ViewAction) {
		switch action {
		case .didTapRemoveAddressButton:
			self = .view(.didTapRemoveAddressButton)
		case .didTapAddressField:
			self = .view(.didTapAddressField)
		case .didTapManageLanes:
			self = .view(.didTapManageLanes)
		case let .binding(action):
			self = .binding(action.pullback(\AlleyEditor.State.view))
		}
	}
}
