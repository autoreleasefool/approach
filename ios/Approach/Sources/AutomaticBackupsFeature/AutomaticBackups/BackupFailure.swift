import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct BackupFailure: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let errorDescription: String
		public var daysSinceLastBackup: DaysSince
		public var daysSinceLastExport: DaysSince

		public init(errorDescription: String) {
			self.errorDescription = errorDescription

			@Dependency(\.date) var date
			@Dependency(ExportService.self) var export
			@Dependency(BackupsService.self) var backups
			daysSinceLastBackup = backups.lastSuccessfulBackupDate()?.daysSince(date()) ?? .never
			daysSinceLastExport = export.lastExportDate()?.daysSince(date()) ?? .never
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didTapOpenSettingsButton
			case didTapDismissButton
		}
		@CasePathable public enum Internal { case doNothing }
		@CasePathable public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(\.dismiss) var dismiss
	@Dependency(BackupsService.self) var backups
	@Dependency(ExportService.self) var exports
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapDismissButton, .didTapOpenSettingsButton:
					return .run { _ in await dismiss() }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .doNothing:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: BackupFailure.self)
public struct BackupFailureView: View {
	@Bindable public var store: StoreOf<BackupFailure>

	public init(store: StoreOf<BackupFailure>) {
		self.store = store
	}

	public var body: some View {
		VStack(spacing: 0) {
			ScrollView {
				VStack(spacing: .standardSpacing) {
					titleSection
					infoSection
					syncStatusSection
				}
				.padding(.horizontal)
			}

			Divider()
				.padding(.vertical)

			Button { send(.didTapOpenSettingsButton) } label: {
				Text(Strings.Backups.Error.FailedToBackup.openSettings)
					.frame(maxWidth: .infinity)
					.padding(.vertical, .unitSpacing)
			}
			.modifier(PrimaryButton())
			.padding(.horizontal)
			.padding(.bottom, .smallSpacing)

			Button { send(.didTapDismissButton) } label: {
				Text(Strings.Action.dismiss)
					.frame(maxWidth: .infinity)
					.padding(.vertical, .unitSpacing)
			}
			.buttonStyle(.borderless)
			.padding(.horizontal)
			.padding(.bottom)
		}
		.presentationDetents([.medium, .large])
		.presentationDragIndicator(.hidden)
	}

	private var titleSection: some View {
		HStack(spacing: 0) {
			// TODO: Use custom image for backup failures
			Image(asset: Asset.Media.Error.notFound)
				.resizable()
				.scaledToFit()
				.frame(width: .largeIcon, height: .largeIcon)
				.padding(.smallSpacing)

			VStack(alignment: .leading, spacing: .tinySpacing) {
				Text(Strings.Backups.Error.FailedToBackup.title)
					.font(.headline)

				Text(Strings.Backups.Error.FailedToBackup.subtitle)
			}
			.frame(maxWidth: .infinity, alignment: .leading)
			.padding()
		}
		.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.strokeBorder(Asset.Colors.Error.default.swiftUIColor, lineWidth: 1)
		)
	}

	private var infoSection: some View {
		Text(Strings.Backups.Error.FailedToBackup.instructions)
			.frame(maxWidth: .infinity, alignment: .leading)
			.padding()
			.background(
				RoundedRectangle(cornerRadius: .standardRadius)
					.fill(Asset.Colors.Background.secondary.swiftUIColor)
			)
	}

	private var syncStatusSection: some View {
		VStack(alignment: .leading, spacing: 0) {
			Text(Strings.Backups.Error.FailedToBackup.SyncStatus.title)
				.font(.headline)
				.padding(.bottom, .standardSpacing)

			let daysSinceLastBackup = switch store.daysSinceLastBackup {
			case let .days(days):
				Strings.Backups.Error.FailedToBackup.SyncStatus.timeSinceLastBackup(days)
			case .never:
				Strings.Backups.Error.FailedToBackup.SyncStatus.neverBackedUp
			}

			let (backupForeground, backupBackground) = store.daysSinceLastBackup.bannerStyle()
			Text(daysSinceLastBackup)
				.foregroundStyle(backupForeground)
				.frame(maxWidth: .infinity, alignment: .leading)
				.padding()
				.background(
					RoundedRectangle(cornerRadius: .standardRadius)
						.fill(backupBackground)
				)
				.padding(.bottom, .smallSpacing)

			let daysSinceLastExport = switch store.daysSinceLastExport {
			case let .days(days):
				Strings.Backups.Error.FailedToBackup.SyncStatus.timeSinceLastExport(days)
			case .never:
				Strings.Backups.Error.FailedToBackup.SyncStatus.neverExported
			}

			let (exportForeground, exportBackground) = store.daysSinceLastExport.bannerStyle()
			Text(daysSinceLastExport)
				.foregroundStyle(exportForeground)
				.frame(maxWidth: .infinity, alignment: .leading)
				.padding()
				.background(
					RoundedRectangle(cornerRadius: .standardRadius)
						.fill(exportBackground)
				)
		}
		.padding()
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(Asset.Colors.Background.secondary.swiftUIColor)
		)
	}
}

#Preview {
	BackupFailureView(
		store: Store(
			initialState: BackupFailure.State(errorDescription: "")
		) {
			BackupFailure()
		}
	)
}
