import AddressLookupFeature
import AlleysRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormLibrary
import Foundation
import LaneEditorFeature
import LanesRepositoryInterface
import LocationsRepositoryInterface
import ModelsLibrary
import StringsLibrary

public typealias AlleyForm = Form<Alley.Create, Alley.Edit>

public struct AlleyEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var name: String
		@BindingState public var material: Alley.Material?
		@BindingState public var pinFall: Alley.PinFall?
		@BindingState public var mechanism: Alley.Mechanism?
		@BindingState public var pinBase: Alley.PinBase?
		public var location: Location.Edit?

		public var existingLanes: IdentifiedArrayOf<Lane.Edit>
		public var newLanes: IdentifiedArrayOf<Lane.Create>

		public let initialValue: AlleyForm.Value
		public var _form: AlleyForm.State

		public let hasLanesEnabled: Bool

		@PresentationState public var addressLookup: AddressLookup.State?
		@PresentationState public var alleyLanesEditor: AlleyLanesEditor.State?

		public init(value: InitialValue) {
			switch value {
			case let .create(new):
				self.name = new.name
				self.material = new.material
				self.pinFall = new.pinFall
				self.mechanism = new.mechanism
				self.pinBase = new.pinBase
				self.existingLanes = []
				self.newLanes = []
				self.initialValue = .create(new)
			case let .edit(existing):
				self.name = existing.alley.name
				self.material = existing.alley.material
				self.pinFall = existing.alley.pinFall
				self.mechanism = existing.alley.mechanism
				self.pinBase = existing.alley.pinBase
				self.existingLanes = existing.lanes
				self.location = existing.alley.location
				self.newLanes = []
				self.initialValue = .edit(existing.alley)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)

			@Dependency(\.featureFlags) var featureFlags
			self.hasLanesEnabled = featureFlags.isEnabled(.lanes)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapRemoveAddressButton
			case didTapAddressField
			case didTapManageLanes
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didCreateLanes(TaskResult<Alley.Create>)
			case didUpdateLanes(TaskResult<Alley.Edit>)
			case form(AlleyForm.Action)
			case addressLookup(PresentationAction<AddressLookup.Action>)
			case alleyLanesEditor(PresentationAction<AlleyLanesEditor.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public enum InitialValue {
		case create(Alley.Create)
		case edit(Alley.EditWithLanes)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.lanes) var lanes

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			AlleyForm()
				.dependency(\.records, .init(
					create: alleys.create,
					update: alleys.update,
					delete: alleys.delete
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapRemoveAddressButton:
					state.location = nil
					// TODO: Fix bug with .didTapRemoveAddressButton needing to set `addressLookup` to nil
					// The problem is the buttons overlap in AlleyEditorView
					state.addressLookup = nil
					return .none

				case .didTapAddressField:
					state.addressLookup = .init(initialQuery: state.location?.title ?? "")
					return .none

				case .didTapManageLanes:
					state.alleyLanesEditor = .init(
						alley: state.alleyId,
						existingLanes: state.existingLanes,
						newLanes: state.newLanes
					)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didCreateLanes(result):
					return state._form.didFinishCreating(result)
						.map { .internal(.form($0)) }

				case let .didUpdateLanes(result):
					return state._form.didFinishUpdating(result)
						.map { .internal(.form($0)) }

				case let .form(.delegate(formAction)):
					switch formAction {
					case let .didCreate(.failure(error)):
						return state._form.didFinishCreating(.failure(error))
							.map { .internal(.form($0)) }

					case let .didUpdate(.failure(error)):
						return state._form.didFinishUpdating(.failure(error))
							.map { .internal(.form($0)) }

					case let .didDelete(.failure(error)):
						return state._form.didFinishDeleting(.failure(error))
							.map { .internal(.form($0)) }

					case let .didCreate(.success(new)):
						return .run { [newLanes = state.newLanes, existingLanes = state.existingLanes] send in
							try await lanes.create(Array(newLanes))
							try await lanes.update(Array(existingLanes))
							await send(.internal(.didCreateLanes(.success(new))))
						}
						catch: { error, send in
							await send(.internal(.didCreateLanes(.failure(error))))
						}

					case let .didUpdate(.success(existing)):
						return .run { [newLanes = state.newLanes, existingLanes = state.existingLanes] send in
							try await lanes.create(Array(newLanes))
							try await lanes.update(Array(existingLanes))
							await send(.internal(.didUpdateLanes(.success(existing))))
						} catch: { error, send in
							await send(.internal(.didUpdateLanes(.failure(error))))
						}

					case let .didDelete(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .run { _ in await self.dismiss() }
					}

				case let .alleyLanesEditor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .alleyLanesEditor(.dismiss):
					guard let newLanes = state.alleyLanesEditor?.newLanes,
								let existingLanes = state.alleyLanesEditor?.existingLanes else {
						return .none
					}
					state.newLanes = newLanes.filter { !$0.label.isEmpty }
					state.existingLanes = existingLanes
					return .none

				case .addressLookup(.dismiss):
					guard let result = state.addressLookup?.lookUpResult else { return .none }
					if state.location == nil {
						state.location = result
					} else {
						state.location?.updateProperties(with: result)
					}
					return .none

				case let .addressLookup(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .alleyLanesEditor(.presented(.internal)),
						.alleyLanesEditor(.presented(.view)):
					return .none

				case .form(.view), .form(.internal):
					return .none

				case .addressLookup(.presented(.binding)),
						.addressLookup(.presented(.internal)),
						.addressLookup(.presented(.view)):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
		.ifLet(\.$addressLookup, action: /Action.internal..Action.InternalAction.addressLookup) {
			AddressLookup()
		}
		.ifLet(\.$alleyLanesEditor, action: /Action.internal..Action.InternalAction.alleyLanesEditor) {
			AlleyLanesEditor()
		}
	}
}

extension AlleyEditor.State {
	var alleyId: Alley.ID {
		switch initialValue {
		case let .create(create): return create.id
		case let .edit(edit): return edit.id
		}
	}

	var form: AlleyForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.material = material
				new.pinFall = pinFall
				new.mechanism = mechanism
				new.pinBase = pinBase
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.material = material
				existing.pinFall = pinFall
				existing.mechanism = mechanism
				existing.pinBase = pinBase
				existing.location = location
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension Alley.Create: CreateableRecord {
	public static var modelName = Strings.Alley.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Alley.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
}
