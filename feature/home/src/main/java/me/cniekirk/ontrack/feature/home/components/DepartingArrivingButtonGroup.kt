package me.cniekirk.ontrack.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import me.cniekirk.ontrack.feature.home.R
import me.cniekirk.ontrack.feature.home.state.QueryType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DepartingArrivingButtonGroup(
    modifier: Modifier = Modifier,
    queryType: QueryType,
    onQueryTypeChanged: (QueryType) -> Unit
) {
    val options = listOf(
        stringResource(R.string.departures_title),
        stringResource(R.string.arrivals_title)
    )
    val icons =
        listOf(
            Icons.Outlined.ArrowUpward,
            Icons.Outlined.ArrowDownward,
        )

    FlowRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = queryType.ordinal == index,
                onCheckedChange = {
                    val newQueryType = if (index == 0) QueryType.DEPARTURES else QueryType.ARRIVALS
                    onQueryTypeChanged(newQueryType)
                },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
            ) {
                Icon(
                    imageVector = icons[index],
                    contentDescription = options[index],
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
