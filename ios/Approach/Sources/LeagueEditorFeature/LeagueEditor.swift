import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import FormFeature
import LeaguesRepositoryInterface
import MapKit
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI

public typealias LeagueForm = FormFeature.Form<League.Create, League.Edit>

@Reducer
public struct LeagueEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var name: String
		public var recurrence: League.Recurrence
		public var defaultNumberOfGames: Int
		public var additionalPinfall: String
		public var additionalGames: String
		public var excludeFromStatistics: League.ExcludeFromStatistics
		public var mapPosition: MapCameraPosition
		public var location: Alley.Summary?

		public var gamesPerSeries: GamesPerSeries
		public var hasAdditionalPinfall: Bool
		public var shouldShowLocationSection: Bool

		public let initialValue: LeagueForm.Value
		public var form: LeagueForm.State

		var isDismissDisabled: Bool { alleyPicker != nil }
		var isEditing: Bool {
			switch initialValue {
			case .create: false
			case .edit: true
			}
		}

		@Presents public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State?

		public init(value: LeagueForm.Value) {
			let defaultNumberOfGames: Int
			let additionalGames: Int
			switch value {
			case let .create(new):
				self.name = new.name
				self.recurrence = new.recurrence
				let additionalPinfall = new.additionalPinfall ?? 0
				self.additionalPinfall = additionalPinfall > 0 ? String(additionalPinfall) : ""
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				self.mapPosition = .automatic
				defaultNumberOfGames = new.defaultNumberOfGames ?? 0
				additionalGames = new.additionalGames ?? 0
				self.gamesPerSeries = .dynamic
				self.shouldShowLocationSection = new.recurrence.shouldShowLocationSection
				self.initialValue = .create(new)
			case let .edit(existing):
				self.name = existing.name
				self.recurrence = existing.recurrence
				let additionalPinfall = existing.additionalPinfall ?? 0
				self.additionalPinfall = additionalPinfall > 0 ? String(additionalPinfall) : ""
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				self.mapPosition = existing.location?.location?.coordinate.mapPosition ?? .automatic
				defaultNumberOfGames = existing.defaultNumberOfGames ?? 0
				additionalGames = existing.additionalGames ?? 0
				self.shouldShowLocationSection = existing.recurrence.shouldShowLocationSection
				self.gamesPerSeries = defaultNumberOfGames == 0 ? .dynamic : .static
				self.initialValue = .edit(existing)
			}
			self.form = .init(initialValue: self.initialValue)
			self.hasAdditionalPinfall = additionalGames > 0
			self.defaultNumberOfGames = max(defaultNumberOfGames, 1)
			self.additionalGames = additionalGames > 0 ? String(additionalGames) : ""
		}

		mutating func syncFormSharedState() {
			switch initialValue {
			case var .create(new):
				new.name = name
				new.recurrence = recurrence
				new.defaultNumberOfGames = gamesPerSeries == .static ? max(1, Int(defaultNumberOfGames)) : nil
				new.additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
				new.additionalPinfall = hasAdditionalPinfall && (new.additionalGames ?? 0) > 0 ? Int(additionalPinfall) : nil
				new.excludeFromStatistics = excludeFromStatistics
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
				existing.additionalPinfall =
					hasAdditionalPinfall && (existing.additionalGames ?? 0) > 0 ? Int(additionalPinfall) : nil
				existing.excludeFromStatistics = excludeFromStatistics
				existing.location = location
				form.value = .edit(existing)
			}
		}
	}

	public enum GamesPerSeries: Int, Equatable, Identifiable, CaseIterable, CustomStringConvertible {
		case `static`
		case dynamic

		public var description: String {
			switch self {
			case .static: return Strings.League.Editor.Fields.GamesPerSeries.constant
			case .dynamic: return Strings.League.Editor.Fields.GamesPerSeries.alwaysAskMe
			}
		}

		public var id: Int { rawValue }
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapAlley
		}
		@CasePathable public enum Delegate {
			case didFinishCreating(League.Create)
			case didFinishUpdating(League.Edit)
			case didFinishArchiving(League.Edit)
		}
		@CasePathable public enum Internal {
			case setLocationSection(isShown: Bool)
			case form(LeagueForm.Action)
			case alleyPicker(PresentationAction<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(AlleysRepository.self) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(LeaguesRepository.self) var leagues
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.form, action: \.internal.form) {
			LeagueForm()
				.dependency(RecordPersistence(
					create: leagues.create,
					update: leagues.update,
					delete: { _ in },
					archive: leagues.archive
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapAlley:
					state.alleyPicker = .init(
						selected: Set([state.location?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .setLocationSection(isShown):
					state.shouldShowLocationSection = isShown
					return .none

				case let .alleyPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(alley):
						state.location = alley.first
						state.mapPosition = state.location?.location?.coordinate.mapPosition ?? .automatic
						state.syncFormSharedState()
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state.form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state.form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didArchive(result):
						return state.form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case let  .didFinishArchiving(league):
						return .concatenate(
							.send(.delegate(.didFinishArchiving(league))),
							.run { _ in await dismiss() }
						)

					case let  .didFinishUpdating(league):
						return .concatenate(
							.send(.delegate(.didFinishUpdating(league))),
							.run { _ in await dismiss() }
						)

					case let  .didFinishCreating(league):
						return .concatenate(
							.send(.delegate(.didFinishCreating(league))),
							.run { _ in await dismiss() }
						)

					case .didDiscard, .didDelete, .didFinishDeleting:
						return .run { _ in await dismiss() }
					}

				case .form(.internal), .form(.view):
					return .none

				case .alleyPicker(.presented(.internal)), .alleyPicker(.presented(.view)), .alleyPicker(.dismiss):
					return .none
				}

			case .binding(\.recurrence):
				switch state.recurrence {
				case .once:
					state.gamesPerSeries = .static
					state.syncFormSharedState()
					return .run { send in await send(.internal(.setLocationSection(isShown: true)), animation: .easeInOut) }
				case .repeating:
					state.location = nil
					state.syncFormSharedState()
					return .run { send in await send(.internal(.setLocationSection(isShown: false)), animation: .easeInOut) }
				}

			case .binding:
				state.syncFormSharedState()
				return .none

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyPicker, action: \.internal.alleyPicker) {
			ResourcePicker { _ in alleys.pickable() }
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.League.Created()
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.League.Updated()
			case .internal(.form(.delegate(.didFinishArchiving))):
				return Analytics.League.Archived()
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
	}
}

extension League.Create: CreateableRecord {
	public static let modelName = Strings.League.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension League.Edit: EditableRecord {
	public var isDeleteable: Bool { false }
	public var isArchivable: Bool { true }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension League.Recurrence: CustomStringConvertible {
	public var description: String {
		switch self {
		case .repeating: return Strings.League.Properties.Recurrence.repeats
		case .once: return Strings.League.Properties.Recurrence.never
		}
	}

	var shouldShowLocationSection: Bool {
		switch self {
		case .repeating:
			return false
		case .once:
			return true
		}
	}
}

extension League.ExcludeFromStatistics: CustomStringConvertible {
	public var description: String {
		switch self {
		case .include: return Strings.League.Properties.ExcludeFromStatistics.include
		case .exclude: return Strings.League.Properties.ExcludeFromStatistics.exclude
		}
	}
}

extension Location.Coordinate {
	var mapPosition: MapCameraPosition {
		.region(.init(center: mapCoordinate, latitudinalMeters: 200, longitudinalMeters: 200))
	}
}
