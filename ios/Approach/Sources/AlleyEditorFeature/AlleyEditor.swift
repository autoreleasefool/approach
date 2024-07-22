import AddressLookupFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import FormFeature
import Foundation
import LaneEditorFeature
import LanesRepositoryInterface
import LocationsRepositoryInterface
import MapKit
import ModelsLibrary
import StringsLibrary
import SwiftUI

public typealias AlleyForm = FormFeature.Form<Alley.Create, Alley.Edit>

@Reducer
public struct AlleyEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var name: String
		public var material: Alley.Material?
		public var pinFall: Alley.PinFall?
		public var mechanism: Alley.Mechanism?
		public var pinBase: Alley.PinBase?
		public var mapPosition: MapCameraPosition
		public var location: Location.Edit?

		public var existingLanes: IdentifiedArrayOf<Lane.Edit>
		public var newLanes: IdentifiedArrayOf<Lane.Create>

		public let initialValue: AlleyForm.Value
		public var form: AlleyForm.State

		@Presents public var destination: Destination.State?

		public init(value: InitialValue) {
			switch value {
			case let .create(new):
				self.name = new.name
				self.material = new.material
				self.pinFall = new.pinFall
				self.mechanism = new.mechanism
				self.pinBase = new.pinBase
				self.mapPosition = .automatic
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
				if let location = existing.alley.location {
					self.mapPosition = location.coordinate.mapPosition
				} else {
					self.mapPosition = .automatic
				}
				self.newLanes = []
				self.initialValue = .edit(existing.alley)
			}
			self.form = .init(initialValue: self.initialValue)
		}

		mutating func syncFormSharedState() {
			switch form.initialValue {
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
		}

		var alleyId: Alley.ID {
			switch initialValue {
			case let .create(create): return create.id
			case let .edit(edit): return edit.id
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapAddressField
			case didTapManageLanes
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didCreateLanes(Result<Alley.Create, Error>)
			case didUpdateLanes(Result<Alley.Edit, Error>)
			case form(AlleyForm.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case addressLookup(AddressLookup)
		case alleyLanes(AlleyLanesEditor)
	}

	public enum InitialValue {
		case create(Alley.Create)
		case edit(Alley.EditWithLanes)
	}

	public init() {}

	@Dependency(AlleysRepository.self) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(LanesRepository.self) var lanes

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.form, action: \.internal.form) {
			AlleyForm()
				.dependency(RecordPersistence(
					create: alleys.create,
					update: alleys.update,
					delete: alleys.delete,
					archive: { _ in }
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapAddressField:
					if state.location == nil {
						state.destination = .addressLookup(.init(initialQuery: state.location?.title ?? ""))
					} else {
						state.location = nil
						state.mapPosition = .automatic
					}
					return .none

				case .didTapManageLanes:
					state.destination = .alleyLanes(.init(
						alley: state.alleyId,
						existingLanes: state.existingLanes,
						newLanes: state.newLanes
					))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didCreateLanes(result):
					return state.form.didFinishCreating(result)
						.map { .internal(.form($0)) }

				case let .didUpdateLanes(result):
					return state.form.didFinishUpdating(result)
						.map { .internal(.form($0)) }

				case let .form(.delegate(formAction)):
					switch formAction {
					case let .didCreate(.failure(error)):
						return state.form.didFinishCreating(.failure(error))
							.map { .internal(.form($0)) }

					case let .didUpdate(.failure(error)):
						return state.form.didFinishUpdating(.failure(error))
							.map { .internal(.form($0)) }

					case let .didDelete(.failure(error)):
						return state.form.didFinishDeleting(.failure(error))
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
						return state.form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishDeleting, .didFinishUpdating, .didDiscard, .didArchive, .didFinishArchiving:
						return .run { _ in await dismiss() }
					}

				case .destination(.dismiss):
					switch state.destination {
					case let .alleyLanes(alleyLanes):
						state.newLanes = alleyLanes.newLanes.filter { !$0.label.isEmpty }
						state.existingLanes = alleyLanes.existingLanes
						return .none
					case let .addressLookup(addressLookup):
						guard let result = addressLookup.lookUpResult else { return .none }
						if state.location == nil {
							state.location = result
						} else {
							state.location!.updateProperties(with: result)
						}

						state.mapPosition = state.location!.coordinate.mapPosition

						state.syncFormSharedState()
						return .none
					case .none:
						return .none
					}

				case .destination(.presented(.alleyLanes(.delegate(.doNothing)))),
						.destination(.presented(.addressLookup(.delegate(.doNothing)))),
						.destination(.presented(.alleyLanes(.internal))),
						.destination(.presented(.alleyLanes(.view))),
						.destination(.presented(.addressLookup(.internal))),
						.destination(.presented(.addressLookup(.view))),
						.destination(.presented(.addressLookup(.binding))),
						.form(.view), .form(.internal):
					return .none
				}

			case .binding:
				state.syncFormSharedState()
				return .none

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case let .internal(.form(.delegate(.didFinishCreating(alley)))):
				let numberOfLanes = state.newLanes.count + state.existingLanes.count
				return Analytics.Alley.Created(withLocation: alley.location != nil, numberOfLanes: numberOfLanes)
			case let .internal(.form(.delegate(.didFinishUpdating(alley)))):
				let numberOfLanes = state.newLanes.count + state.existingLanes.count
				return Analytics.Alley.Updated(withLocation: alley.location != nil, numberOfLanes: numberOfLanes)
			case .internal(.form(.delegate(.didFinishDeleting))):
				return Analytics.Alley.Deleted()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.form(.delegate(.didCreate(.failure(error))))),
				let .internal(.form(.delegate(.didUpdate(.failure(error))))),
				let .internal(.form(.delegate(.didDelete(.failure(error))))):
				return error
			default:
				return nil
			}
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
	public var isArchivable: Bool { false }
}

extension Location.Coordinate {
	var mapPosition: MapCameraPosition {
		.region(.init(center: mapCoordinate, latitudinalMeters: 200, longitudinalMeters: 200))
	}
}
